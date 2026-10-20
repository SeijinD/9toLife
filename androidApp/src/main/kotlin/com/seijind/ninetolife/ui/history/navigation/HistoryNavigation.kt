package com.seijind.ninetolife.ui.history.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalResources
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.seijind.ninetolife.R
import com.seijind.ninetolife.ui.app.navigation.HistoryKey
import com.seijind.ninetolife.ui.common.ObserveAsEvents
import com.seijind.ninetolife.ui.common.navigation.slideTransition
import com.seijind.ninetolife.ui.common.showUndoSnackbar
import com.seijind.ninetolife.ui.history.composable.HistoryContent
import com.seijind.ninetolife.ui.history.model.HistoryActions
import com.seijind.ninetolife.ui.history.model.HistoryEvent
import com.seijind.ninetolife.ui.history.viewmodel.HistoryViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

internal fun EntryProviderScope<NavKey>.historyEntry(onBack: () -> Unit) {
    entry<HistoryKey>(metadata = slideTransition()) {
        val viewModel: HistoryViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val resources = LocalResources.current
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        ObserveAsEvents(flow = viewModel.events) { event ->
            when (event) {
                is HistoryEvent.ShiftDeleted -> scope.launch {
                    val result = snackbarHostState.showUndoSnackbar(
                        message = resources.getString(R.string.history_shift_deleted),
                        undoLabel = resources.getString(R.string.common_undo),
                    )
                    if (result == SnackbarResult.ActionPerformed) viewModel.onUndoDelete(shift = event.shift)
                }
            }
        }

        HistoryContent(
            uiState = uiState,
            actions = HistoryActions(
                onBackClick = onBack,
                onPreviousMonthClick = viewModel::onPreviousMonthClick,
                onNextMonthClick = viewModel::onNextMonthClick,
                onDayClick = viewModel::onDayClick,
                onDeleteShift = viewModel::onDeleteShift,
            ),
            snackbarHostState = snackbarHostState,
        )
    }
}
