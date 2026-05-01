package com.example.hackchallenge.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay

/**
 * Reactive relative-time label. Recomposes every 30s so "Just now" becomes "1m ago"
 * without needing a manual refresh.
 */
@Composable
fun rememberRelativeTime(timestampMillis: Long): String {
    val now by produceState(initialValue = System.currentTimeMillis(), key1 = timestampMillis) {
        while (true) {
            value = System.currentTimeMillis()
            delay(30_000L)
        }
    }
    return formatRelative(now - timestampMillis)
}

private fun formatRelative(deltaMs: Long): String {
    if (deltaMs < 0) return "Just now"
    val seconds = deltaMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    return when {
        seconds < 30 -> "Just now"
        minutes < 1 -> "${seconds}s ago"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        days < 30 -> "${days / 7}w ago"
        days < 365 -> "${days / 30}mo ago"
        else -> "${days / 365}y ago"
    }
}
