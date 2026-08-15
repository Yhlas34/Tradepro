package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Path
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoldYellow
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChartImagePicker(
    selectedBitmap: Bitmap?,
    onBitmapSelected: (Bitmap) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    onBitmapSelected(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        bitmap?.let {
            onBitmapSelected(it)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .testTag("chart_image_picker_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Grafik Ekran Görüntüsü Yükleyin veya Çekin:",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "TradingView, Binance, XM veya MetaTrader grafik görüntüsünü yükleyerek anında AI analizi alın.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Image Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                    .testTag("image_preview_box"),
                contentAlignment = Alignment.Center
            ) {
                if (selectedBitmap != null) {
                    Image(
                        bitmap = selectedBitmap.asImageBitmap(),
                        contentDescription = "Seçilen Grafik",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Görsel Hazır",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge,
                            color = BullishGreen
                        )
                    }
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAPhoto,
                            contentDescription = "Fotoğraf Yükle",
                            tint = ElectricCyan,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Grafik fotoğrafı / ekran görüntüsü seçilmedi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                        Text(
                            text = "Aşağıdaki butonları veya örnek demoları kullanın",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons: Gallery & Camera
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("gallery_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricBlue
                    )
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Galeriden Seç", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("camera_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kamerayı Aç", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Preset Demo Chart Buttons (Instant Test)
            Text(
                text = "Hızlı Test İçin Örnek Borsa Grafikleri:",
                style = MaterialTheme.typography.labelLarge,
                color = GoldYellow
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip(
                    title = "BTC/USDT (Crypto)",
                    subtitle = "4H SMC Chart",
                    onClick = {
                        val btcBitmap = createSampleChartBitmap("BTC/USDT 4H SMC", "#00E676")
                        onBitmapSelected(btcBitmap)
                    },
                    modifier = Modifier.weight(1f).testTag("preset_btc")
                )

                PresetChip(
                    title = "XAU/USD (Gold)",
                    subtitle = "1H Forex Chart",
                    onClick = {
                        val goldBitmap = createSampleChartBitmap("XAU/USD 1H Gold", "#FFD700")
                        onBitmapSelected(goldBitmap)
                    },
                    modifier = Modifier.weight(1f).testTag("preset_gold")
                )

                PresetChip(
                    title = "EUR/USD (FX)",
                    subtitle = "15m Scalp",
                    onClick = {
                        val fxBitmap = createSampleChartBitmap("EUR/USD 15m Scalp", "#00E5FF")
                        onBitmapSelected(fxBitmap)
                    },
                    modifier = Modifier.weight(1f).testTag("preset_eur")
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = DarkSurfaceVariant,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted
            )
        }
    }
}

// Generate realistic candlestick chart sample bitmaps programmatically
private fun createSampleChartBitmap(title: String, colorHex: String): Bitmap {
    val width = 800
    val height = 450
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Dark Background
    val bgPaint = Paint().apply { color = AndroidColor.parseColor("#0B0F17") }
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

    // Grid lines
    val gridPaint = Paint().apply {
        color = AndroidColor.parseColor("#1B2436")
        strokeWidth = 1f
    }
    for (i in 0..height step 50) {
        canvas.drawLine(0f, i.toFloat(), width.toFloat(), i.toFloat(), gridPaint)
    }
    for (j in 0..width step 80) {
        canvas.drawLine(j.toFloat(), 0f, j.toFloat(), height.toFloat(), gridPaint)
    }

    // Candlesticks
    val greenPaint = Paint().apply { color = AndroidColor.parseColor("#00E676") }
    val redPaint = Paint().apply { color = AndroidColor.parseColor("#FF5252") }
    val wickPaint = Paint().apply { strokeWidth = 2f }

    val startX = 60f
    val candleWidth = 18f
    val candleGap = 24f

    var curY = 220f
    val random = java.util.Random(1234)

    for (k in 0..25) {
        val cx = startX + k * candleGap
        val change = (random.nextFloat() - 0.42f) * 60f
        val openY = curY
        val closeY = curY - change
        val highY = Math.min(openY, closeY) - (random.nextFloat() * 25f)
        val lowY = Math.max(openY, closeY) + (random.nextFloat() * 25f)

        val isBull = closeY < openY
        val candleColor = if (isBull) greenPaint else redPaint
        wickPaint.color = candleColor.color

        // Draw Wick
        canvas.drawLine(cx + candleWidth / 2, highY, cx + candleWidth / 2, lowY, wickPaint)

        // Draw Candle Body
        val top = Math.min(openY, closeY)
        val bottom = Math.max(openY, closeY)
        canvas.drawRect(cx, top, cx + candleWidth, Math.max(bottom, top + 2f), candleColor)

        curY = closeY
    }

    // Title & Indicator Overlay Text
    val textPaint = Paint().apply {
        color = AndroidColor.parseColor(colorHex)
        textSize = 32f
        isFakeBoldText = true
    }
    canvas.drawText("TradeAI Pro: $title", 40f, 50f, textPaint)

    val subPaint = Paint().apply {
        color = AndroidColor.parseColor("#94A3B8")
        textSize = 22f
    }
    canvas.drawText("RSI(14): 48.5 | EMA 200 Support | SMC Liquidity Order Block Detected", 40f, 90f, subPaint)

    return bitmap
}
