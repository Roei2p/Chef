package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.timer.TimerState

@Composable
fun SmartTimerFloatingBar(
    timerState: TimerState,
    onTogglePlayPause: () -> Unit,
    onAddMinute: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = timerState.initialSeconds > 0

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        val minutes = timerState.remainingSeconds / 60
        val seconds = timerState.remainingSeconds % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = if (timerState.isFinished) MaterialTheme.colorScheme.primaryContainer else Color(0xFF1E2822),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("smart_timer_floating_bar")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Timer Info & Countdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (timerState.isFinished) Color.White.copy(alpha = 0.25f)
                                else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (timerState.isFinished) Icons.Default.NotificationsActive else Icons.Default.Timer,
                            contentDescription = null,
                            tint = if (timerState.isFinished) Color.White else MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (timerState.isFinished) "השלב הסתיים! בדוק את המנה" else timerState.title,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = timeFormatted,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = if (timerState.isFinished) Color(0xFFFFD54F) else Color.White
                        )
                    }
                }

                // Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Text(
                            text = "+1 דק׳",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .clickable { onAddMinute() }
                        )
                    }

                    IconButton(
                        onClick = onTogglePlayPause,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (timerState.isRunning) "השהה" else "הפעל",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(
                        onClick = onReset,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "איפוס טיימר",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
