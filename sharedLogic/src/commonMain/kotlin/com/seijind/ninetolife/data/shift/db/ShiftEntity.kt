package com.seijind.ninetolife.data.shift.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "shifts", indices = [Index("started_at")])
internal data class ShiftEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "started_at") val startedAtEpochMillis: Long,
    @ColumnInfo(name = "ended_at") val endedAtEpochMillis: Long,
    @ColumnInfo(name = "planned_minutes") val plannedMinutes: Int,
)
