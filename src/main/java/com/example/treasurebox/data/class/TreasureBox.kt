package com.example.treasurebox.data.`class`

data class TreasureBox(
    val id: String,
    val name: String,
    val currentBalance: Double,
    val coverImageUrl: String,
    val members: List<User>
)