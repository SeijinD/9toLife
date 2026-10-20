package com.seijind.ninetolife.data.shift.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ShiftDao {

    @Insert
    suspend fun insert(shift: ShiftEntity): Long

    @Query("DELETE FROM shifts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM shifts WHERE started_at >= :fromEpochMillis AND started_at < :toEpochMillis ORDER BY started_at")
    fun observeStartedBetween(fromEpochMillis: Long, toEpochMillis: Long): Flow<List<ShiftEntity>>

    @Query(
        "SELECT COUNT(*) AS shiftCount, " +
            "COALESCE(SUM(MAX(0, (ended_at - started_at) / 60000 - planned_minutes)), 0) AS overtimeMinutes " +
            "FROM shifts",
    )
    fun observeTotals(): Flow<ShiftTotalsRow>
}
