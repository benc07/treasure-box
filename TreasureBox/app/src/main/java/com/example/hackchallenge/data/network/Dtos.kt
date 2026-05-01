package com.example.hackchallenge.data.network

import com.example.hackchallenge.data.SampleData
import com.example.hackchallenge.model.Transaction
import com.example.hackchallenge.model.TransactionKind
import com.example.hackchallenge.model.TreasureBox
import com.example.hackchallenge.model.User
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Locale


@Serializable
data class BoxesEnvelope(val boxes: List<BoxDto>)

@Serializable
data class BoxDto(
    val id: Int,
    val name: String,
    val target_amount: Double,
    val balance: Double,
    val cover_image: String? = null,
    val collaborators: List<String> = emptyList(),
    val created_at: String,
    val transactions: List<TransactionDto> = emptyList()
)

@Serializable
data class TransactionDto(
    val id: Int,
    val box_id: Int,
    val type: String,
    val label: String,
    val amount: Double,
    val note: String? = null,
    val created_by: String? = null,
    val timestamp: String
)

@Serializable
data class CreateBoxRequest(
    val name: String,
    val target_amount: Double,
    val collaborators: List<String> = emptyList(),
    val cover_image: String? = null
)

@Serializable
data class CreateTransactionRequest(
    val type: String,           // "deposit" or "withdraw"
    val label: String,
    val amount: Double,
    val note: String? = null,
    val created_by: String? = null
)


fun BoxDto.toDomain(): TreasureBox = TreasureBox(
    id = id.toString(),
    name = name,
    coverImageUrl = cover_image.orEmpty(),
    currentBalance = balance,
    target = target_amount,
    members = listOf(SampleData.currentUser) + collaborators.mapIndexed { idx, handle ->
        User(
            id = handle,
            name = handle,
            avatarUrl = SampleData.avatarPool[(idx + 1) % SampleData.avatarPool.size]
        )
    },
    transactions = transactions.map { it.toDomain() }.reversed() // server returns oldest-first; UI shows newest-first
)

fun TransactionDto.toDomain(): Transaction = Transaction(
    id = id.toString(),
    kind = if (type.equals("deposit", ignoreCase = true)) {
        TransactionKind.DEPOSIT
    } else {
        TransactionKind.WITHDRAW
    },
    title = label,
    amount = amount,
    author = User(
        id = created_by ?: "anon",
        name = created_by ?: "Someone",
        avatarUrl = SampleData.currentUser.avatarUrl
    ),
    createdAtMillis = parsePythonTimestamp(timestamp),
    note = note,
    comments = emptyList() // backend has no comments yet; merged client-side in repo
)

// Python's `str(datetime.now())` produces "2026-04-30 14:23:45.123456".
// We try a couple of common forms then fall back to "now" if parsing fails so the UI
// never crashes on a malformed timestamp.
private val pythonFormats = listOf(
    "yyyy-MM-dd HH:mm:ss.SSSSSS",
    "yyyy-MM-dd HH:mm:ss.SSS",
    "yyyy-MM-dd HH:mm:ss"
)

private fun parsePythonTimestamp(s: String): Long {
    for (pattern in pythonFormats) {
        runCatching {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            return sdf.parse(s)?.time ?: continue
        }
    }
    return System.currentTimeMillis()
}
