package com.seijind.ninetolife.ui.history.model

import com.seijind.ninetolife.domain.shift.model.CompletedShift

internal data class HistoryActions(
    val onBackClick: () -> Unit,
    val onPreviousMonthClick: () -> Unit,
    val onNextMonthClick: () -> Unit,
    val onDayClick: (dayOfMonth: Int) -> Unit,
    val onDeleteShift: (shift: CompletedShift) -> Unit,
)
