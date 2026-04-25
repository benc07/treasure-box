package com.example.treasurebox.data.repository

import com.example.treasurebox.data.`class`.TreasureBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class TreasureBoxRepository(
    //TODO update api service
    private val boxApiService: BoxApiService
) {
    private val _allBoxes = MutableStateFlow<List<TreasureBox>>(emptyList())

    fun getTreasureBoxes(): Flow<List<TreasureBox>> = _allBoxes.asStateFlow()

    fun getBoxById(id: String): TreasureBox? {
        return _allBoxes.value.find { it.id == id }
    }

    fun createBox(name: String, coverImage: String) {
        val newBox = TreasureBox(
            id = UUID.randomUUID().toString(),
            name = name,
            currentBalance = 0.0,
            coverImageUrl = coverImage,
            members = emptyList()
        )
        _allBoxes.update { current -> current + newBox }
    }

    fun updateBalance(boxId: String, amount: Double) {
        _allBoxes.update { list ->
            list.map {
                if (it.id == boxId) it.copy(currentBalance = it.currentBalance + amount)
                else it
            }
        }
    }
}