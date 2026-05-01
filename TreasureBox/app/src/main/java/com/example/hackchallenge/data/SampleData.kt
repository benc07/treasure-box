package com.example.hackchallenge.data

import com.example.hackchallenge.model.Comment
import com.example.hackchallenge.model.Transaction
import com.example.hackchallenge.model.TransactionKind
import com.example.hackchallenge.model.TreasureBox
import com.example.hackchallenge.model.User

object SampleData {

    val avatarPool: List<String> = listOf(
        "https://api.dicebear.com/7.x/avataaars/png?seed=Zayed",
        "https://api.dicebear.com/7.x/avataaars/png?seed=Maya",
        "https://api.dicebear.com/7.x/avataaars/png?seed=Eleene",
        "https://api.dicebear.com/7.x/avataaars/png?seed=Riley"
    )

    val currentUser = User(
        id = "u_zayed",
        name = "Zayed",
        avatarUrl = avatarPool[0]
    )
    private val maya = User(
        id = "u_maya",
        name = "Maya",
        avatarUrl = avatarPool[1]
    )
    private val eleene = User(
        id = "u_eleene",
        name = "Eleene",
        avatarUrl = avatarPool[2]
    )

    private val now = System.currentTimeMillis()
    private const val MINUTE = 60_000L
    private const val HOUR = 60 * MINUTE
    private const val DAY = 24 * HOUR

    val sampleBox = TreasureBox(
        id = "box_japan_2026",
        name = "Japan Trip 2026",
        coverImageUrl = "https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?w=1200",
        currentBalance = 1190.00,
        target = 4000.00,
        members = listOf(currentUser, maya, eleene),
        transactions = listOf(
            Transaction(
                id = "t1",
                kind = TransactionKind.DEPOSIT,
                title = "Monthly Contribution",
                amount = 250.00,
                author = maya,
                createdAtMillis = now - 2 * HOUR,
                comments = listOf(
                    Comment("c1", currentUser, "Let's go!! 🎉", now - 1 * HOUR),
                    Comment("c2", eleene, "Almost there!", now - 45 * MINUTE)
                )
            ),
            Transaction(
                id = "t2",
                kind = TransactionKind.WITHDRAW,
                title = "Hotel deposit (Kyoto)",
                amount = 180.00,
                author = currentUser,
                createdAtMillis = now - 1 * DAY,
                note = "Booked the ryokan for night 3",
                comments = listOf(
                    Comment("c3", maya, "ahhh excited 🥳", now - 20 * HOUR)
                )
            ),
            Transaction(
                id = "t3",
                kind = TransactionKind.DEPOSIT,
                title = "Birthday gift from grandma",
                amount = 500.00,
                author = eleene,
                createdAtMillis = now - 3 * DAY
            ),
            Transaction(
                id = "t4",
                kind = TransactionKind.DEPOSIT,
                title = "Sold old camera",
                amount = 220.00,
                author = currentUser,
                createdAtMillis = now - 7 * DAY
            )
        )
    )
}
