package com.example.ui.screens

import com.example.ui.components.ChartImagePicker
import com.example.ui.components.PnLCalculatorCard
import com.example.ui.components.SignalCard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TradingViewModel

@Composable
fun ChartAnalysisScreen(
    viewModel: TradingViewModel,
    onNavigateToMt5: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBitmap by viewModel.selectedBitmap.collectAsStateWithLifecycle()
    val isAnalyzing by viewModel.isAnalyzing.collectAsStateWithLifecycle()
    val currentSignal by viewModel.currentSignal.collectAsStateWithLifecycle()
    val analysisMessage by viewModel.analysisMessage.collectAsStateWithLifecycle()

    val balanceInput by viewModel.accountBalanceInput.collectAsStateWithLifecycle()
    val leverageInput by viewModel.leverageInput.collectAsStateWithLifecycle()
    val riskPercentInput by viewModel.riskPercentInput.collectAsStateWithLifecycle()

    var userNoteText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(analysisMessage) {
        analysisMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "TradeAI Logo",
                        tint = BullishGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TradeAI Pro Vision Analyzer",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Yapay zeka görsel grafik analizi, Long/Short sinyalleri & SL/TP kar hesabı",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Chart Screenshot & Camera Picker Component
            ChartImagePicker(
                selectedBitmap = selectedBitmap,
                onBitmapSelected = { bitmap ->
                    viewModel.setSelectedBitmap(bitmap)
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // User Optional Note Input
            OutlinedTextField(
                value = userNoteText,
                onValueChange = { userNoteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_note_input"),
                label = { Text("Özel Analiz Notu (İsteğe Bağlı)", color = TextMuted) },
                placeholder = { Text("Ör. SMC Order Block ve 4H FVG likiditesine göre bak", color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = ElectricCyan) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main CTA Button: Analyze Chart with Gemini AI
            Button(
                onClick = {
                    selectedBitmap?.let { bitmap ->
                        viewModel.analyzeBitmap(bitmap, userNoteText)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("analyze_chart_button"),
                enabled = selectedBitmap != null && !isAnalyzing,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BullishGreen,
                    contentColor = DarkBackground,
                    disabledContainerColor = DarkSurfaceVariant,
                    disabledContentColor = TextMuted
                )
            ) {
                if (isAnalyzing) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = BullishGreen,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Yapay Zeka Grafik Analizi Yapıyor...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Analiz Et",
                            tint = DarkBackground
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedBitmap == null) "Önce Grafik Yükleyin / Seçin" else "Grafiği Yapay Zeka ile Analiz Et",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = DarkBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Generated Signal & SL/TP Display
            currentSignal?.let { signal ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                ) {
                    Column {
                        // Signal Card
                        SignalCard(
                            signal = signal,
                            onExecuteMt5 = { sig ->
                                viewModel.executeSignalOnMt5(sig)
                                onNavigateToMt5()
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Automatic P&L & Risk Calculator Card
                        PnLCalculatorCard(
                            signal = signal,
                            balanceInput = balanceInput,
                            onBalanceChange = viewModel::updateAccountBalanceInput,
                            leverageInput = leverageInput,
                            onLeverageChange = viewModel::updateLeverageInput,
                            riskPercentInput = riskPercentInput,
                            onRiskPercentChange = viewModel::updateRiskPercentInput
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
