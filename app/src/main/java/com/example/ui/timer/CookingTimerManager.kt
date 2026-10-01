package com.example.ui.timer

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TimerState(
    val title: String = "טיימר בישול",
    val initialSeconds: Int = 0,
    val remainingSeconds: Int = 0,
    val isRunning: Boolean = false,
    val isFinished: Boolean = false,
    val activeRecipeTitle: String? = null
)

class CookingTimerManager(
    private val context: Context,
    private val scope: CoroutineScope
) {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun startTimer(title: String, seconds: Int, recipeTitle: String? = null) {
        timerJob?.cancel()
        _timerState.value = TimerState(
            title = title,
            initialSeconds = seconds,
            remainingSeconds = seconds,
            isRunning = true,
            isFinished = false,
            activeRecipeTitle = recipeTitle
        )

        timerJob = scope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                val current = _timerState.value.remainingSeconds
                if (current <= 1) {
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = 0,
                        isRunning = false,
                        isFinished = true
                    )
                    triggerVibration()
                    break
                } else {
                    _timerState.value = _timerState.value.copy(
                        remainingSeconds = current - 1
                    )
                }
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resumeTimer() {
        if (_timerState.value.remainingSeconds > 0 && !_timerState.value.isRunning) {
            val title = _timerState.value.title
            val remaining = _timerState.value.remainingSeconds
            val recipe = _timerState.value.activeRecipeTitle
            startTimer(title, remaining, recipe)
        }
    }

    fun addSeconds(seconds: Int) {
        val newRemaining = _timerState.value.remainingSeconds + seconds
        val newInitial = _timerState.value.initialSeconds + seconds
        _timerState.value = _timerState.value.copy(
            remainingSeconds = newRemaining,
            initialSeconds = newInitial,
            isFinished = false
        )
        if (!_timerState.value.isRunning && newRemaining > 0) {
            resumeTimer()
        }
    }

    fun resetTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState()
    }

    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createWaveform(longArrayOf(0, 400, 200, 400, 200, 600), -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 400, 200, 400, 200, 600), -1)
            }
        } catch (_: Exception) {
            // Ignore if vibration service not supported in testing environment
        }
    }
}
