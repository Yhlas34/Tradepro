package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ChartAnalysisScreen
import com.example.ui.screens.Mt5AccountScreen
import com.example.ui.screens.SignalHistoryScreen
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
import com.example.ui.theme.TradeAITheme
import com.example.viewmodel.TradingViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object ChartAnalysis : Screen("chart_analysis", "Grafik Analizi", Icons.Default.ShowChart)
    object Mt5Account : Screen("mt5_account", "XM / MT5 Hesabı", Icons.Default.AccountBalance)
    object History : Screen("signal_history", "Sinyal Geçmişi", Icons.Default.History)
    object AiAssistant : Screen("ai_assistant", "AI Asistan", Icons.Default.Psychology)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeAITheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: TradingViewModel = viewModel()
    var showCustomizationDialog by remember { mutableStateOf(false) }

    val navItems = listOf(
        Screen.ChartAnalysis,
        Screen.Mt5Account,
        Screen.History,
        Screen.AiAssistant
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceVariant,
                contentColor = TextPrimary,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                navItems.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = if (isSelected) BullishGreen else TextMuted
                            )
                        },
                        label = {
                            Text(
                                text = screen.title,
                                color = if (isSelected) BullishGreen else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        selected = isSelected,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = BullishGreen.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCustomizationDialog = true },
                containerColor = ElectricBlue,
                contentColor = TextPrimary,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("customization_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = "Soru & Özelleştirme")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Uygulama İpuçları & Soru-Cevap", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.ChartAnalysis.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.ChartAnalysis.route) {
                ChartAnalysisScreen(
                    viewModel = viewModel,
                    onNavigateToMt5 = {
                        navController.navigate(Screen.Mt5Account.route)
                    }
                )
            }

            composable(Screen.Mt5Account.route) {
                Mt5AccountScreen(
                    viewModel = viewModel
                )
            }

            composable(Screen.History.route) {
                SignalHistoryScreen(
                    viewModel = viewModel,
                    onNavigateToMt5 = {
                        navController.navigate(Screen.Mt5Account.route)
                    }
                )
            }

            composable(Screen.AiAssistant.route) {
                AiAssistantScreen(
                    viewModel = viewModel
                )
            }
        }

        // Customization & Question Dialog requested by user
        if (showCustomizationDialog) {
            FeatureQuestionnaireDialog(
                onDismiss = { showCustomizationDialog = false }
            )
        }
    }
}

@Composable
fun FeatureQuestionnaireDialog(onDismiss: () -> Unit) {
    val selectedFeatures = remember {
        mutableStateListOf(
            "Telegram & Discord Otomatik Sinyal Bildirim Botu",
            "TradingView Webhook Entegrasyonu",
            "Canlı Mum Hareketlerini Kamerayla Otomatik Tarama",
            "Trailing Stop (İzleyen Stop Loss) Desteği"
        )
    }

    val suggestedFeatures = listOf(
        "Telegram & Discord Otomatik Sinyal Bildirim Botu",
        "TradingView Webhook Entegrasyonu",
        "Canlı Mum Hareketlerini Kamerayla Otomatik Tarama",
        "Trailing Stop (İzleyen Stop Loss) Desteği",
        "Ekstra XM Çoklu Hesap Yöneticisi (Multi-Account)",
        "Geriye Dönük Backtest ve Başarı Oranı Simülatörü"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GoldYellow)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gelişmiş Özelleştirme Soruları",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "Uygulamanıza aşağıdaki ek özellikleri eklememizi ister misiniz? Seçtiğiniz modüller bir sonraki sürümde aktif edilecektir:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                suggestedFeatures.forEach { feature ->
                    val isChecked = selectedFeatures.contains(feature)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (isChecked) selectedFeatures.remove(feature) else selectedFeatures.add(feature)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (it) selectedFeatures.add(feature) else selectedFeatures.remove(feature)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BullishGreen,
                                uncheckedColor = DarkCardBorder
                            )
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("📌 Kullanım İpuçları:", style = MaterialTheme.typography.labelLarge, color = ElectricCyan)
                        Text("1. Kamera ile ekran görüntüsü veya TradingView grafiği çekip anında yükleyebilirsiniz.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                        Text("2. 'XM & MT5 Hesabı' sayfasında hesap numaranızı ve sunucu bilgilerinizi bağlayarak otomatik işlem açabilirsiniz.", style = MaterialTheme.typography.bodyMedium, color = TextMuted)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Seçimleri Kaydet ve Kapat", color = BullishGreen, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
