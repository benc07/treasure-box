package com.example.treasurebox.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.treasurebox.data.`class`.TreasureBox
import com.example.treasurebox.data.repository.TreasureBoxRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

data class BoxUiState(
    val isLoading: Boolean = false,
    val boxes: List<TreasureBox> = emptyList(),
    val errorMessage: String? = null
)

class MainViewModel(
    private val boxRepository: TreasureBoxRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(BoxUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        observeBoxes()
    }
    private fun observeBoxes() {
        viewModelScope.launch {
            boxRepository.getTreasureBoxes()
                .collect { updatedBoxes ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            boxes = updatedBoxes
                        )
                    }
                }
        }
    }

    fun addBox(name: String, coverImage: String) {
        boxRepository.createBox(name, coverImage)
    }
}