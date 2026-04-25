package com.example.treasurebox.data.`class`

import java.util.UUID

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String
) {
    val avatarUrl: String = ""//TODO
}
