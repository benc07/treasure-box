package com.example.hackchallenge.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AmountKeypad(
    onKey: (String) -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf(".", "0", "<")
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                row.forEach { key ->
                    KeypadKey(
                        label = key,
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp),
                        onClick = {
                            if (key == "<") onBackspace() else onKey(key)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (label == "<") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Backspace",
                tint = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = label,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

object AmountInput {
    fun apply(current: String, key: String): String {
        if (key == ".") {
            if (current.contains(".")) return current
            return if (current.isEmpty()) "0." else "$current."
        }
        val dotIdx = current.indexOf(".")
        if (dotIdx >= 0 && current.length - dotIdx > 2) return current
        if (current == "0") return key
        if (current.length >= 9) return current
        return current + key
    }

    fun backspace(current: String): String =
        if (current.isEmpty()) current else current.dropLast(1)

    fun toAmount(input: String): Double = input.toDoubleOrNull() ?: 0.0

    fun displayString(input: String): String {
        if (input.isEmpty()) return "0"
        val dotIdx = input.indexOf('.')
        val intPart = if (dotIdx >= 0) input.substring(0, dotIdx) else input
        val fracPart = if (dotIdx >= 0) input.substring(dotIdx) else "" // keeps the "." and trailing digits as-typed

        val formattedInt = intPart.toLongOrNull()?.let {
            java.text.NumberFormat.getIntegerInstance(java.util.Locale.US).format(it)
        } ?: intPart.ifEmpty { "0" }

        return formattedInt + fracPart
    }
}

@Composable
fun AmountDisplay(
    raw: String,
    modifier: Modifier = Modifier,
    prefix: String = "$",
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onBackground
) {
    Row(
        modifier = modifier.padding(vertical = 24.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = prefix,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 10.dp, end = 4.dp)
        )
        Text(
            text = AmountInput.displayString(raw),
            fontSize = 64.sp,
            fontWeight = FontWeight.Black,
            color = valueColor
        )
    }
}
