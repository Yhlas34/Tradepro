package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.TradeSignal
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

@Composable
fun PnLCalculatorCard(
    signal: TradeSignal,
    balanceInput: String,
    onBalanceChange: (String) -> Unit,
    leverageInput: String,
    onLeverageChange: (String) -> Unit,
    riskPercentInput: String,
    onRiskPercentChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val balance = balanceInput.toDoubleOrNull() ?: 5000.0
    val leverage = leverageInput.toDoubleOrNull() ?: 20.0
    val riskPercent = riskPercentInput.toDoubleOrNull() ?: 2.0

    // Calculations according to signal prices
    val isLong = signal.signalType == "LONG"
    val entry = signal.entryPrice
    val sl = signal.stopLoss
    val tp1 = signal.takeProfit1
    val tp2 = signal.takeProfit2
    val tp3 = signal.takeProfit3

    // Distance to SL & TPs
    val slDistance = abs(entry - sl)
    val tp1Distance = abs(tp1 - entry)
    val tp2Distance = abs(tp2 - entry)
    val tp3Distance = abs(tp3 - entry)

    // Max risk amount in dollars
    val maxRiskDollars = (balance * (riskPercent / 100.0))

    // Position size estimation (in USD position value and approximate lots)
    val slPercentChange = if (entry > 0) (slDistance / entry) else 0.01
    val positionSizeUsd = if (slPercentChange > 0) (maxRiskDollars / slPercentChange) else maxRiskDollars * 10
    val approxLotSize = (positionSizeUsd / 100000.0) // standard lot unit

    // P&L projections
    val lossAtSl = maxRiskDollars
    val profitAtTp1 = if (slDistance > 0) (tp1Distance / slDistance) * maxRiskDollars else maxRiskDollars * 2
    val profitAtTp2 = if (slDistance > 0) (tp2Distance / slDistance) * maxRiskDollars else maxRiskDollars * 3
    val profitAtTp3 = if (slDistance > 0) (tp3Distance / slDistance) * maxRiskDollars else maxRiskDollars * 4.5

    val formatter = remember {
        NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 2
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .testTag("pnl_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Calculate,
                    contentDescription = "Hesaplayıcı",
                    tint = ElectricBlue,
                    modifier = Modifier.height(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Otomatik Kar/Zarar & Risk Hesaplayıcı",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Seçili ${signal.symbol} sinyaline göre sermaye ve kaldıracınıza özel kar/zarar hesabı:",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // User Inputs: Balance & Leverage & Risk %
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Balance Input
                OutlinedTextField(
                    value = balanceInput,
                    onValueChange = onBalanceChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("balance_input"),
                    label = { Text("Bakiye ($)", color = TextMuted) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldYellow) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Leverage Input
                OutlinedTextField(
                    value = leverageInput,
                    onValueChange = onLeverageChange,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("leverage_input"),
                    label = { Text("Kaldıraç (x)", color = TextMuted) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null, tint = ElectricBlue) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Risk % Slider & Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = null,
                        tint = BearishRed,
                        modifier = Modifier.height(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "İşlem Başına Risk %:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "%${String.format(Locale.US, "%.1f", riskPercent)} (${formatter.format(maxRiskDollars)})",
                    style = MaterialTheme.typography.titleMedium,
                    color = BearishRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = riskPercent.toFloat(),
                onValueChange = { onRiskPercentChange(String.format(Locale.US, "%.1f", it)) },
                valueRange = 0.5f..10.0f,
                steps = 19,
                modifier = Modifier.testTag("risk_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = BearishRed,
                    activeTrackColor = BearishRed,
                    inactiveTrackColor = DarkSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Position Size Stats Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Hesaplanan Pozisyon Büyüklüğü", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                        Text(
                            text = "${formatter.format(positionSizeUsd)} USD",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Önerilen Lot Büyüklüğü", style = MaterialTheme.typography.labelLarge, color = TextMuted)
                        Text(
                            text = "${String.format(Locale.US, "%.2f", approxLotSize.coerceAtLeast(0.01))} Lot",
                            style = MaterialTheme.typography.titleMedium,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Estimated Profits & Loss Breakdown Table
            Text(
                text = "Tahmini Kar / Zarar Tablosu (P&L):",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // SL Row
            PnLRow(
                title = "Stop Loss (SL) Vurursa:",
                amountText = "-${formatter.format(lossAtSl)}",
                percentText = "-%${String.format(Locale.US, "%.1f", riskPercent)}",
                color = BearishRed,
                isLoss = true
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TP1 Row
            PnLRow(
                title = "Take Profit 1 (TP1) Ulaşırsa:",
                amountText = "+${formatter.format(profitAtTp1)}",
                percentText = "+%${String.format(Locale.US, "%.1f", (profitAtTp1 / balance) * 100)}",
                color = BullishGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TP2 Row
            PnLRow(
                title = "Take Profit 2 (TP2) Ulaşırsa:",
                amountText = "+${formatter.format(profitAtTp2)}",
                percentText = "+%${String.format(Locale.US, "%.1f", (profitAtTp2 / balance) * 100)}",
                color = BullishGreen
            )

            Spacer(modifier = Modifier.height(6.dp))

            // TP3 Row
            PnLRow(
                title = "Take Profit 3 (TP3) Ulaşırsa:",
                amountText = "+${formatter.format(profitAtTp3)}",
                percentText = "+%${String.format(Locale.US, "%.1f", (profitAtTp3 / balance) * 100)}",
                color = BullishGreen
            )
        }
    }
}

@Composable
private fun PnLRow(
    title: String,
    amountText: String,
    percentText: String,
    color: androidx.compose.ui.graphics.Color,
    isLoss: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, color.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = amountText,
                    style = MaterialTheme.typography.titleMedium,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = color.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = percentText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
