package com.seijind.ninetolife.data.shift.db

/** Room result of [ShiftDao.observeTotals]; the column names match the query aliases. */
internal data class ShiftTotalsRow(
    val shiftCount: Int,
    val overtimeMinutes: Long,
)
