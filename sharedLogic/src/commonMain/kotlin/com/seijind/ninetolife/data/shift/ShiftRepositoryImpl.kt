package com.seijind.ninetolife.data.shift

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.seijind.ninetolife.data.shift.db.ShiftDao
import com.seijind.ninetolife.data.shift.db.ShiftEntity
import com.seijind.ninetolife.domain.shift.ShiftRepository
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.domain.shift.model.ShiftTotals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** The running shift lives in DataStore (one row of state); finished shifts go to Room. */
internal class ShiftRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val shiftDao: ShiftDao,
) : ShiftRepository {

    override val activeShift: Flow<ActiveShift?> = dataStore.data
        .map { preferences ->
            val startedAt = preferences[StartedAtKey]
            val planned = preferences[PlannedMinutesKey]
            if (startedAt == null || planned == null) {
                null
            } else {
                ActiveShift(startedAtEpochMillis = startedAt, plannedMinutes = planned)
            }
        }
        .distinctUntilChanged()

    override val totals: Flow<ShiftTotals> = shiftDao.observeTotals()
        .map { row -> ShiftTotals(shiftCount = row.shiftCount, overtimeMinutes = row.overtimeMinutes) }

    override suspend fun setActiveShift(shift: ActiveShift?) {
        dataStore.edit { preferences ->
            if (shift == null) {
                preferences.remove(key = StartedAtKey)
                preferences.remove(key = PlannedMinutesKey)
            } else {
                preferences[StartedAtKey] = shift.startedAtEpochMillis
                preferences[PlannedMinutesKey] = shift.plannedMinutes
            }
        }
    }

    override suspend fun saveCompleted(
        startedAtEpochMillis: Long,
        endedAtEpochMillis: Long,
        plannedMinutes: Int,
    ): CompletedShift {
        val entity = ShiftEntity(
            startedAtEpochMillis = startedAtEpochMillis,
            endedAtEpochMillis = endedAtEpochMillis,
            plannedMinutes = plannedMinutes,
        )
        val id = shiftDao.insert(shift = entity)
        return entity.copy(id = id).toDomain()
    }

    override suspend fun deleteCompleted(id: Long) = shiftDao.deleteById(id = id)

    override fun observeStartedBetween(fromEpochMillis: Long, toEpochMillis: Long): Flow<List<CompletedShift>> =
        shiftDao.observeStartedBetween(fromEpochMillis = fromEpochMillis, toEpochMillis = toEpochMillis)
            .map { shifts -> shifts.map { it.toDomain() } }

    private fun ShiftEntity.toDomain(): CompletedShift = CompletedShift(
        id = id,
        startedAtEpochMillis = startedAtEpochMillis,
        endedAtEpochMillis = endedAtEpochMillis,
        plannedMinutes = plannedMinutes,
    )

    private companion object {
        val StartedAtKey = longPreferencesKey(name = "active_shift_started_at")
        val PlannedMinutesKey = intPreferencesKey(name = "active_shift_planned_minutes")
    }
}
