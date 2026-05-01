package com.example.hackchallenge.ui.util

import java.text.NumberFormat
import java.util.Locale

private val moneyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

fun Double.asMoney(): String = moneyFormat.format(this)
