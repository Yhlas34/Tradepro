package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.Mt5AccountCard
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
import kotlinx.coroutines.launch

@Composable
fun Mt5AccountScreen(
    viewModel: TradingViewModel,
    modifier: Modifier = Modifier
) {
    val mt5Config by viewModel.mt5Config.collectAsStateWithLifecycle()
    val openPositions by viewModel.openPositions.collectAsStateWithLifecycle()

    var serverInput by remember(mt5Config.serverName) { mutableStateOf(mt5Config.serverName) }
    var accountInput by remember(mt5Config.accountNumber) { mutableStateOf(mt5Config.accountNumber) }
    var passwordInput by remember { mutableStateOf("••••••••••••") }
    var isDemoState by remember(mt5Config.isDemo) { mutableStateOf(mt5Config.isDemo) }
    var leverageState by remember(mt5Config.leverage) { mutableStateOf(mt5Config.leverage.toString()) }
    var autoExecuteState by remember(mt5Config.autoExecuteSignals) { mutableStateOf(mt5Config.autoExecuteSignals) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
            // Screen Title Banner
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
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "XM MT5",
                        tint = GoldYellow,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "XM & MetaTrader 5 Hesabı",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Hesap detaylarınızı bağlayın, açık pozisyonlarınızı ve otomatik emre dönüşüm ayarlarını yönetin.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Current Account Live Status & Open Positions Component
            Mt5AccountCard(
                account = mt5Config,
                openPositions = openPositions,
                onToggleConnection = {
                    viewModel.toggleMt5Connection()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (mt5Config.isConnected) "XM / MT5 Sunucu bağlantısı kesildi" else "XM / MT5 Sunucusuna başarıyla bağlanıldı!"
                        )
                    }
                },
                onClosePosition = { ticket ->
                    viewModel.closePosition(ticket)
                    scope.launch {
                        snackbarHostState.showSnackbar("#$ticket numaralı pozisyon başarıyla kapatıldı.")
                    }
                },
                onAutoExecuteToggle = { enabled ->
                    autoExecuteState = enabled
                    viewModel.updateMt5Config(
                        serverName = serverInput,
                        accountNumber = accountInput,
                        isDemo = isDemoState,
                        leverage = leverageState.toIntOrNull() ?: 100,
                        autoExecute = enabled,
                        maxRisk = mt5Config.maxRiskPercent
                    )
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (enabled) "XM Otomatik Sinyal İletimi Etkinleştirildi" else "Otomatik Sinyal İletimi Durduruldu"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // XM & MT5 API Connection Settings Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                    .testTag("mt5_settings_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ayar",
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "XM / MT5 Sunucu & API Giriş Ayarları",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Demo / Real Account Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShieldMoon, contentDescription = null, tint = GoldYellow)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isDemoState) "Demo Hesabı Kullan (XM-Demo)" else "Gerçek Hesap Kullan (XM-Real)",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }

                        Switch(
                            checked = isDemoState,
                            onCheckedChange = { isDemoState = it },
                            modifier = Modifier.testTag("demo_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = GoldYellow,
                                checkedTrackColor = GoldYellow.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // XM Server Name Input
                    OutlinedTextField(
                        value = serverInput,
                        onValueChange = { serverInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mt5_server_input"),
                        label = { Text("XM / Broker Sunucu Adı", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Dns, contentDescription = null, tint = ElectricCyan) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Account Number Input
                    OutlinedTextField(
                        value = accountInput,
                        onValueChange = { accountInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mt5_account_num_input"),
                        label = { Text("MT5 Hesap / Login Numarası", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ElectricBlue) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mt5_password_input"),
                        label = { Text("MT5 Şifre / Investor Password", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = GoldYellow) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Leverage Input
                    OutlinedTextField(
                        value = leverageState,
                        onValueChange = { leverageState = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mt5_leverage_setting"),
                        label = { Text("Kaldıraç Oranı (1:100, 1:200, 1:500)", color = TextMuted) },
                        leadingIcon = { Icon(Icons.Default.CloudDone, contentDescription = null, tint = BullishGreen) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Config Button
                    Button(
                        onClick = {
                            viewModel.updateMt5Config(
                                serverName = serverInput,
                                accountNumber = accountInput,
                                isDemo = isDemoState,
                                leverage = leverageState.toIntOrNull() ?: 100,
                                autoExecute = autoExecuteState,
                                maxRisk = mt5Config.maxRiskPercent
                            )
                            scope.launch {
                                snackbarHostState.showSnackbar("XM / MT5 Sunucu bağlantı yapılandırması kaydedildi!")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("save_mt5_config_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = TextPrimary
                        )
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ayarları Kaydet ve XM'e Bağlan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
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
