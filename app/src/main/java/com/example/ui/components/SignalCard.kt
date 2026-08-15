package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradeSignal
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BearishRedContainer
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.BullishGreenContainer
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SignalCard(
    signal: TradeSignal,
    onExecuteMt5: ((TradeSignal) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    val isLong = signal.signalType == "LONG"
    val mainColor = if (isLong) BullishGreen else BearishRed
    val containerColor = if (isLong) BullishGreenContainer else BearishRedContainer

    val numberFormat = remember {
        NumberFormat.getNumberInstance(Locale.US).apply {
            maximumFractionDigits = if (signal.entryPrice < 10) 4 else 2
            minimumFractionDigits = if (signal.entryPrice < 10) 2 else 2
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .testTag("signal_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header Row: Symbol, Timeframe, Signal Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.symbol,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = signal.timeframe,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = ElectricCyan
                        )
                    }
                }

                // Long/Short Badge
                Surface(
                    color = containerColor,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, mainColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isLong) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = signal.signalType,
                            tint = mainColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isLong) "LONG (ALIM)" else "SHORT (SATIŞ)",
                            style = MaterialTheme.typography.labelLarge,
                            color = mainColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Confidence & Risk/Reward Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Güven Oranı",
                        tint = GoldYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Güven skoru: %${signal.confidence}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Risk/Ödül",
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "R:R = ${signal.riskRewardRatio}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Price Targets Grid: Entry, Stop Loss, TP1, TP2, TP3
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Entry Price
                    PriceLevelRow(
                        label = "Giriş Fiyatı (Entry):",
                        value = "$${numberFormat.format(signal.entryPrice)}",
                        valueColor = TextPrimary,
                        badgeText = "ORDER",
                        badgeColor = ElectricBlue
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stop Loss
                    PriceLevelRow(
                        label = "Stop Loss (SL):",
                        value = "$${numberFormat.format(signal.stopLoss)}",
                        valueColor = BearishRed,
                        badgeText = "DURDUR",
                        badgeColor = BearishRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Take Profit 1
                    PriceLevelRow(
                        label = "Take Profit 1 (TP1):",
                        value = "$${numberFormat.format(signal.takeProfit1)}",
                        valueColor = BullishGreen,
                        badgeText = "KAR 1",
                        badgeColor = BullishGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Take Profit 2
                    PriceLevelRow(
                        label = "Take Profit 2 (TP2):",
                        value = "$${numberFormat.format(signal.takeProfit2)}",
                        valueColor = BullishGreen,
                        badgeText = "KAR 2",
                        badgeColor = BullishGreen
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Take Profit 3
                    PriceLevelRow(
                        label = "Take Profit 3 (TP3):",
                        value = "$${numberFormat.format(signal.takeProfit3)}",
                        valueColor = BullishGreen,
                        badgeText = "KAR 3",
                        badgeColor = BullishGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Key Indicators Chips
            if (signal.keyIndicators.isNotBlank()) {
                Text(
                    text = "Tespit Edilen Teknik Göstergeler:",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    signal.keyIndicators.split(",").forEach { tag ->
                        Surface(
                            color = Color(0xFF1E293B),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkCardBorder)
                        ) {
                            Text(
                                text = "• ${tag.trim()}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Expand / Collapse AI Analysis Rationale
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yapay Zeka Analiz Detayı & Rasyonalite",
                    style = MaterialTheme.typography.titleMedium,
                    color = ElectricCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Detay",
                    tint = ElectricCyan
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = signal.analysisSummary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // Quick One-Tap Execute on XM / MT5 Button
            if (onExecuteMt5 != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onExecuteMt5(signal) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("execute_mt5_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (signal.executedOnMt5) DarkSurfaceVariant else mainColor,
                        contentColor = if (signal.executedOnMt5) BullishGreen else DarkCardBorder
                    ),
                    enabled = !signal.executedOnMt5
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (signal.executedOnMt5) Icons.Default.CheckCircle else Icons.Default.Bolt,
                            contentDescription = "MT5",
                            tint = if (signal.executedOnMt5) BullishGreen else Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (signal.executedOnMt5) "XM/MT5'e İletildi (Aktif)" else "XM & MT5'te Otomatik İşlem Aç",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (signal.executedOnMt5) BullishGreen else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceLevelRow(
    label: String,
    value: String,
    valueColor: Color,
    badgeText: String,
    badgeColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = valueColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = badgeColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = badgeText,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = badgeColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
