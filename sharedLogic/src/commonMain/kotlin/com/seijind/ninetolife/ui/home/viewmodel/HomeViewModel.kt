package com.seijind.ninetolife.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seijind.ninetolife.domain.shift.model.ActiveShift
import com.seijind.ninetolife.domain.shift.model.CompletedShift
import com.seijind.ninetolife.ui.home.model.HomeEvent
import com.seijind.ninetolife.ui.home.model.HomeUiState
import com.seijind.ninetolife.ui.home.model.ShiftProgress
import com.seijind.ninetolife.usecase.settings.ObserveSettingsUseCase
import com.seijind.ninetolife.usecase.shift.CancelShiftStartUseCase
import com.seijind.ninetolife.usecase.shift.EndShiftUseCase
import com.seijind.ninetolife.usecase.shift.ObserveActiveShiftUseCase
import com.seijind.ninetolife.usecase.shift.ObserveShiftTotalsUseCase
import com.seijind.ninetolife.usecase.shift.StartShiftUseCase
import com.seijind.ninetolife.usecase.shift.UndoEndShiftUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

private val TickInterval = 1.seconds
private val StopTickingDelay = 5.seconds

class HomeViewModel(
    private val observeSettings: ObserveSettingsUseCase,
    private val observeActiveShift: ObserveActiveShiftUseCase,
    private val observeShiftTotals: ObserveShiftTotalsUseCase,
    private val startShift: StartShiftUseCase,
    private val cancelShiftStart: CancelShiftStartUseCase,
    private val endShift: EndShiftUseCase,
    private val undoEndShift: UndoEndShiftUseCase,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = HomeUiState())
    private val activeShift = MutableStateFlow<ActiveShift?>(value = null)

    /** [activeShift] starts as null before storage is read, which would look like "no shift". */
    private val isShiftLoaded = MutableStateFlow(value = false)

    /**
     * The per-second ticker is merged in here instead of in [_uiState], so it only runs
     * while someone is watching the screen.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = combine(
        _uiState,
        activeShift.flatMapLatest { shift -> shift?.let { progressTicks(shift = it) } ?: flowOf(value = null) },
    ) { state, progress -> state.copy(shift = progress) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeout = StopTickingDelay),
            initialValue = HomeUiState(),
        )

    private val _events = Channel<HomeEvent>(capacity = Channel.BUFFERED)
    val events: Flow<HomeEvent> = _events.receiveAsFlow()

    private var pendingUndo: PendingUndo? = null
    private var shiftToggleJob: Job? = null

    init {
        viewModelScope.launch {
            observeSettings().collect { settings ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        qrPayload = settings.qrPayload,
                        backgroundImagePath = settings.backgroundImagePath,
                        seriousMode = settings.seriousMode,
                        isQrExpanded = it.isQrExpanded && settings.qrPayload != null,
                    )
                }
            }
        }
        viewModelScope.launch {
            observeActiveShift().collect { shift ->
                activeShift.value = shift
                isShiftLoaded.value = true
            }
        }
        viewModelScope.launch {
            observeShiftTotals().collect { totals -> _uiState.update { it.copy(totals = totals) } }
        }
    }

    fun onAddQrClick() {
        _events.trySend(element = HomeEvent.OpenScanner)
    }

    /** Opening the QR is the clock-in/clock-out gesture: it starts or ends the shift, with undo. */
    fun onQrClick() {
        if (_uiState.value.isQrExpanded) {
            onQrDismiss()
            return
        }
        _uiState.update { it.copy(isQrExpanded = true) }
        if (shiftToggleJob?.isActive == true) return
        shiftToggleJob = viewModelScope.launch {
            // Right after launch the running shift may not be read yet; deciding now could start a second one.
            isShiftLoaded.first { it }
            if (activeShift.value == null) {
                if (!startShift()) return@launch
                pendingUndo = PendingUndo.Start
                _events.trySend(element = HomeEvent.ShiftStarted)
            } else {
                val completed = endShift() ?: return@launch
                pendingUndo = PendingUndo.End(shift = completed)
                _events.trySend(element = HomeEvent.ShiftEnded(workedMinutes = completed.workedMinutes))
            }
        }
    }

    /**
     * The QR shortcut from outside the app: clocks in or out like [onQrClick], but only once the
     * saved QR and the running shift are known, and never closes a QR that is already open.
     */
    fun onQuickQrRequest() {
        viewModelScope.launch {
            _uiState.first { !it.isLoading }
            isShiftLoaded.first { it }
            val state = _uiState.value
            if (state.qrPayload == null || state.isQrExpanded) return@launch
            onQrClick()
        }
    }

    fun onQrDismiss() {
        _uiState.update { it.copy(isQrExpanded = false) }
    }

    fun onUndoClick() {
        val undo = pendingUndo ?: return
        pendingUndo = null
        // Runs as the toggle job, so a QR tap during the undo waits instead of racing it.
        val previous = shiftToggleJob
        shiftToggleJob = viewModelScope.launch {
            previous?.join()
            when (undo) {
                PendingUndo.Start -> cancelShiftStart()
                is PendingUndo.End -> undoEndShift(completed = undo.shift)
            }
            _events.trySend(element = HomeEvent.UndoDone)
        }
    }

    fun onUndoExpired() {
        pendingUndo = null
    }

    private fun progressTicks(shift: ActiveShift): Flow<ShiftProgress> = flow {
        var wasRunning: Boolean? = null
        while (true) {
            val now = clock.now().toEpochMilliseconds()
            val progress = ShiftProgress(
                startedAtEpochMillis = shift.startedAtEpochMillis,
                endsAtEpochMillis = shift.endsAtEpochMillis,
                nowEpochMillis = now,
            )
            if (wasRunning == true && progress.isOvertime) _events.trySend(element = HomeEvent.Freedom)
            wasRunning = !progress.isOvertime
            emit(value = progress)
            // Aligns ticks to whole seconds so the countdown never skips or repeats a digit.
            delay(duration = TickInterval - (now % TickInterval.inWholeMilliseconds).milliseconds)
        }
    }

    private sealed interface PendingUndo {
        data object Start : PendingUndo
        data class End(val shift: CompletedShift) : PendingUndo
    }
}
