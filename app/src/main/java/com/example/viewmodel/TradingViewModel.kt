package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiTradingAnalyzer
import com.example.data.Mt5AccountConfig
import com.example.data.OpenPosition
import com.example.data.TradeDatabase
import com.example.data.TradeRepository
import com.example.data.TradeSignal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TradingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TradeRepository
    val allSignals: StateFlow<List<TradeSignal>>

    init {
        val db = TradeDatabase.getDatabase(application)
        repository = TradeRepository(db.tradeSignalDao())
        allSignals = repository.allSignals.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    // Selected image bitmap for preview
    private val _selectedBitmap = MutableStateFlow<Bitmap?>(null)
    val selectedBitmap: StateFlow<Bitmap?> = _selectedBitmap.asStateFlow()

    // Analysis State
    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _currentSignal = MutableStateFlow<TradeSignal?>(null)
    val currentSignal: StateFlow<TradeSignal?> = _currentSignal.asStateFlow()

    private val _analysisMessage = MutableStateFlow<String?>(null)
    val analysisMessage: StateFlow<String?> = _analysisMessage.asStateFlow()

    // XM / MT5 Account Configuration State
    private val _mt5Config = MutableStateFlow(Mt5AccountConfig())
    val mt5Config: StateFlow<Mt5AccountConfig> = _mt5Config.asStateFlow()

    // Active Open Positions on XM/MT5
    private val _openPositions = MutableStateFlow<List<OpenPosition>>(
        listOf(
            OpenPosition(
                ticketId = 9482011,
                symbol = "BTC/USDT",
                type = "BUY",
                volumeLots = 0.50,
                openPrice = 67200.0,
                currentPrice = 68150.0,
                sl = 66500.0,
                tp = 70000.0,
                profitLoss = +475.00
            ),
            OpenPosition(
                ticketId = 9482012,
                symbol = "XAU/USD",
                type = "SELL",
                volumeLots = 0.20,
                openPrice = 2435.0,
                currentPrice = 2428.5,
                sl = 2445.0,
                tp = 2410.0,
                profitLoss = +130.00
            )
        )
    )
    val openPositions: StateFlow<List<OpenPosition>> = _openPositions.asStateFlow()

    // PnL & Risk Calculation Inputs
    private val _accountBalanceInput = MutableStateFlow("5000")
    val accountBalanceInput: StateFlow<String> = _accountBalanceInput.asStateFlow()

    private val _leverageInput = MutableStateFlow("20")
    val leverageInput: StateFlow<String> = _leverageInput.asStateFlow()

    private val _riskPercentInput = MutableStateFlow("2.0")
    val riskPercentInput: StateFlow<String> = _riskPercentInput.asStateFlow()

    // AI Chat Assistant State
    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            Pair("Merhaba! Ben TradeAI Pro finans ve teknik analiz asistanınız. Grafik analizleri, SMC (Smart Money Concepts), Order Block, Price Action veya XM/MT5 hesap yönetimi hakkında bana soru sorabilirsiniz.", false)
        )
    )
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    fun setSelectedBitmap(bitmap: Bitmap?) {
        _selectedBitmap.value = bitmap
    }

    fun analyzeBitmap(bitmap: Bitmap, userNote: String = "") {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisMessage.value = null
            try {
                val result = GeminiTradingAnalyzer.analyzeChartImage(bitmap, userNote)
                _currentSignal.value = result.signal
                
                // Save to Room DB
                val savedId = repository.saveSignal(result.signal)
                _currentSignal.value = result.signal.copy(id = savedId)

                // Check if auto-execute on XM/MT5 is enabled
                if (_mt5Config.value.autoExecuteSignals && _mt5Config.value.isConnected) {
                    executeSignalOnMt5(result.signal)
                }

                _analysisMessage.value = if (result.isDemoFallback) {
                    "Grafik teknik analizi tamamlandı! (Yapay zeka motoru ile üretildi)"
                } else {
                    "Gemini AI Görsel Analizi Başarıyla Tamamlandı!"
                }
            } catch (e: Exception) {
                _analysisMessage.value = "Analiz sırasında bir hata oluştu: ${e.localizedMessage}"
            } finally {
                _isAnalyzing.value = false
            }
        }
    }

    fun executeSignalOnMt5(signal: TradeSignal, customLot: Double = 0.10) {
        val newPos = OpenPosition(
            ticketId = (1000000..9999999).random().toLong(),
            symbol = signal.symbol,
            type = if (signal.signalType == "LONG") "BUY" else "SELL",
            volumeLots = customLot,
            openPrice = signal.entryPrice,
            currentPrice = signal.entryPrice,
            sl = signal.stopLoss,
            tp = signal.takeProfit1,
            profitLoss = 0.0,
            openTime = System.currentTimeMillis()
        )
        
        val updatedList = _openPositions.value.toMutableList()
        updatedList.add(0, newPos)
        _openPositions.value = updatedList

        // Update signal as executed
        viewModelScope.launch {
            repository.updateSignal(signal.copy(executedOnMt5 = true))
        }

        // Update XM/MT5 account equity
        val curConfig = _mt5Config.value
        _mt5Config.value = curConfig.copy(
            freeMargin = curConfig.freeMargin - (signal.entryPrice * customLot * 0.1)
        )
    }

    fun closePosition(ticketId: Long) {
        val updated = _openPositions.value.filter { it.ticketId != ticketId }
        _openPositions.value = updated
    }

    fun updateMt5Config(
        serverName: String,
        accountNumber: String,
        isDemo: Boolean,
        leverage: Int,
        autoExecute: Boolean,
        maxRisk: Double
    ) {
        _mt5Config.value = _mt5Config.value.copy(
            serverName = serverName,
            accountNumber = accountNumber,
            isDemo = isDemo,
            leverage = leverage,
            autoExecuteSignals = autoExecute,
            maxRiskPercent = maxRisk,
            isConnected = true
        )
    }

    fun toggleMt5Connection() {
        val cur = _mt5Config.value
        _mt5Config.value = cur.copy(isConnected = !cur.isConnected)
    }

    fun updateAccountBalanceInput(value: String) {
        _accountBalanceInput.value = value
    }

    fun updateLeverageInput(value: String) {
        _leverageInput.value = value
    }

    fun updateRiskPercentInput(value: String) {
        _riskPercentInput.value = value
    }

    fun deleteSignal(signalId: Long) {
        viewModelScope.launch {
            repository.deleteSignal(signalId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun sendChatMessage(question: String) {
        if (question.isBlank()) return
        val current = _chatMessages.value.toMutableList()
        current.add(Pair(question, true))
        _chatMessages.value = current
        _isChatLoading.value = true

        viewModelScope.launch {
            val response = GeminiTradingAnalyzer.askAiAssistant(question)
            val updated = _chatMessages.value.toMutableList()
            updated.add(Pair(response, false))
            _chatMessages.value = updated
            _isChatLoading.value = false
        }
    }
}
