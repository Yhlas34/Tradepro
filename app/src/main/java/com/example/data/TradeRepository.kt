package com.example.data

import kotlinx.coroutines.flow.Flow

class TradeRepository(private val dao: TradeSignalDao) {

    val allSignals: Flow<List<TradeSignal>> = dao.getAllSignals()

    suspend fun saveSignal(signal: TradeSignal): Long {
        return dao.insertSignal(signal)
    }

    suspend fun updateSignal(signal: TradeSignal) {
        dao.updateSignal(signal)
    }

    suspend fun deleteSignal(id: Long) {
        dao.deleteSignalById(id)
    }

    suspend fun clearHistory() {
        dao.clearAllSignals()
    }
}
