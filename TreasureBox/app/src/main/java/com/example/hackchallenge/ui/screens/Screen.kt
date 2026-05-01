package com.example.hackchallenge.ui.screens

import kotlinx.serialization.Serializable

@Serializable
data object MainRoute

@Serializable
data object AddBoxRoute

@Serializable
data class BoxDetailRoute(val boxId: String)

@Serializable
data class DepositRoute(val boxId: String)

@Serializable
data class WithdrawRoute(val boxId: String)
