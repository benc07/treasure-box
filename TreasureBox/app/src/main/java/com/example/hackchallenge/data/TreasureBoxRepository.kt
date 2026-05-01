package com.example.hackchallenge.data

import com.example.hackchallenge.data.network.BoxApiService
import com.example.hackchallenge.data.network.CreateBoxRequest
import com.example.hackchallenge.data.network.CreateTransactionRequest
import com.example.hackchallenge.data.network.toDomain
import com.example.hackchallenge.model.Comment
import com.example.hackchallenge.model.TreasureBox
import com.example.hackchallenge.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * API-backed source of truth for boxes.
 *
 * Boxes & transactions are persisted on the Flask backend; comments are kept in-memory
 * (the backend has no comments endpoint yet) and merged onto the server-side data on
 * every refresh so they survive deposits/withdrawals.
 */
class TreasureBoxRepository(
    private val api: BoxApiService
) {
    private val _allBoxes = MutableStateFlow<List<TreasureBox>>(emptyList())
    fun getTreasureBoxes(): Flow<List<TreasureBox>> = _allBoxes.asStateFlow()

    fun getBoxById(id: String): TreasureBox? = _allBoxes.value.find { it.id == id }

    /** Refresh the dashboard list. Boxes from /api/boxes/ don't include transactions. */
    suspend fun refreshAll() {
        val envelope = api.getAllBoxes()
        val serverBoxes = envelope.boxes.map { it.toDomain() }
        _allBoxes.update { previous ->
            serverBoxes.map { fresh -> mergeWithLocal(fresh, previous) }
        }
    }

    /** Refresh one box (and its full transaction list). */
    suspend fun refreshBox(boxId: String) {
        val fresh = api.getBox(boxId.toInt()).toDomain()
        _allBoxes.update { previous ->
            val existsAlready = previous.any { it.id == fresh.id }
            val merged = mergeWithLocal(fresh, previous)
            if (existsAlready) {
                previous.map { if (it.id == merged.id) merged else it }
            } else {
                previous + merged
            }
        }
    }

    suspend fun addBox(
        name: String,
        target: Double,
        coverImage: String?,
        collaborators: List<String>
    ): TreasureBox {
        val created = api.createBox(
            CreateBoxRequest(
                name = name,
                target_amount = target,
                collaborators = collaborators,
                cover_image = coverImage?.takeIf { it.isNotBlank() }
            )
        ).toDomain()
        _allBoxes.update { it + created }
        return created
    }

    suspend fun deposit(boxId: String, title: String, amount: Double, author: User) {
        require(amount > 0) { "Deposit amount must be positive" }
        api.createTransaction(
            boxId = boxId.toInt(),
            req = CreateTransactionRequest(
                type = "deposit",
                label = title,
                amount = amount,
                created_by = author.name
            )
        )
        refreshBox(boxId)
    }

    suspend fun withdraw(
        boxId: String,
        title: String,
        amount: Double,
        note: String?,
        author: User
    ) {
        require(amount > 0) { "Withdraw amount must be positive" }
        api.createTransaction(
            boxId = boxId.toInt(),
            req = CreateTransactionRequest(
                type = "withdraw",
                label = title,
                amount = amount,
                note = note,
                created_by = author.name
            )
        )
        refreshBox(boxId)
    }

    /** Local-only: backend has no comments table. Survives refreshes via [mergeWithLocal]. */
    fun postComment(boxId: String, transactionId: String, text: String, author: User) {
        if (text.isBlank()) return
        val newComment = Comment(
            id = UUID.randomUUID().toString(),
            author = author,
            text = text.trim(),
            createdAtMillis = System.currentTimeMillis()
        )
        _allBoxes.update { list ->
            list.map { box ->
                if (box.id != boxId) return@map box
                box.copy(
                    transactions = box.transactions.map { tx ->
                        if (tx.id == transactionId) {
                            tx.copy(comments = tx.comments + newComment)
                        } else tx
                    }
                )
            }
        }
    }

    /** Carry locally-held comments forward when applying server data. */
    private fun mergeWithLocal(
        serverBox: TreasureBox,
        previousList: List<TreasureBox>
    ): TreasureBox {
        val previous = previousList.find { it.id == serverBox.id } ?: return serverBox
        val byId = previous.transactions.associateBy { it.id }
        return serverBox.copy(
            transactions = serverBox.transactions.map { tx ->
                val localComments = byId[tx.id]?.comments.orEmpty()
                if (localComments.isEmpty()) tx else tx.copy(comments = localComments)
            }
        )
    }
}
