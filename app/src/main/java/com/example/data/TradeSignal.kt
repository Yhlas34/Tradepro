package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trade_signals")
data class TradeSignal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val symbol: String,
    val timeframe: String,
    val signalType: String, // "LONG" or "SHORT"
    val confidence: Int, // e.g. 85%
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit1: Double,
    val takeProfit2: Double,
    val takeProfit3: Double,
    val riskRewardRatio: String,
    val analysisSummary: String,
    val keyIndicators: String,
    val imagePath: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE", // "ACTIVE", "TP1_HIT", "TP2_HIT", "TP3_HIT", "SL_HIT"
    val executedOnMt5: Boolean = false
)

data class Mt5AccountConfig(
    val serverName: String = "XMGlobal-Real 28",
    val accountNumber: String = "8492015",
    val isDemo: Boolean = true,
    val isConnected: Boolean = true,
    val balance: Double = 5000.00,
    val equity: Double = 5240.50,
    val freeMargin: Double = 4800.00,
    val marginLevel: Double = 1191.0,
    val leverage: Int = 100,
    val autoExecuteSignals: Boolean = false,
    val maxRiskPercent: Double = 2.0,
    val trailingStopEnabled: Boolean = true
)

data class OpenPosition(
    val ticketId: Long,
    val symbol: String,
    val type: String, // "BUY" or "SELL"
    val volumeLots: Double,
    val openPrice: Double,
    val currentPrice: Double,
    val sl: Double,
    val tp: Double,
    val profitLoss: Double,
    val openTime: Long = System.currentTimeMillis()
)
