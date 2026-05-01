package com.example.hackchallenge.model

import java.util.UUID

/**
 * Person on the platform. A "member" of a box is just a User added to it.
 *
 * Note (vs Eleene's draft): we use [avatarUrl] (String) instead of avatarRes (Int)
 * so the same model works for hardcoded sample avatars and remote API avatars.
 */
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val avatarUrl: String
)

data class Comment(
    val id: String,
    val author: User,
    val text: String,
    val createdAtMillis: Long
)

enum class TransactionKind { DEPOSIT, WITHDRAW }

data class Transaction(
    val id: String,
    val kind: TransactionKind,
    val title: String,
    val amount: Double,
    val author: User,
    val createdAtMillis: Long,
    val note: String? = null,
    val comments: List<Comment> = emptyList()
)

data class TreasureBox(
    val id: String,
    val name: String,
    val coverImageUrl: String,
    val currentBalance: Double,
    val target: Double,
    val members: List<User>,
    val transactions: List<Transaction> = emptyList()
)
