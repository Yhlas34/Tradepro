package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeSignalDao {

    @Query("SELECT * FROM trade_signals ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<TradeSignal>>

    @Query("SELECT * FROM trade_signals WHERE id = :id")
    suspend fun getSignalById(id: Long): TradeSignal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: TradeSignal): Long

    @Update
    suspend fun updateSignal(signal: TradeSignal)

    @Query("DELETE FROM trade_signals WHERE id = :id")
    suspend fun deleteSignalById(id: Long)

    @Query("DELETE FROM trade_signals")
    suspend fun clearAllSignals()
}
