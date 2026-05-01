package com.example.hackchallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hackchallenge.data.SampleData
import com.example.hackchallenge.data.TreasureBoxRepository
import com.example.hackchallenge.model.TreasureBox
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        refresh()
    }

    private fun observeBoxes() {
        viewModelScope.launch {
            boxRepository.getTreasureBoxes().collect { updated ->
                _uiState.update { it.copy(isLoading = false, boxes = updated) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching { boxRepository.refreshAll() }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Couldn't reach the server"
                        )
                    }
                }
                .onSuccess { _uiState.update { it.copy(isLoading = false) } }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun createAndAddBox(
        name: String,
        currentBalanceStr: String,
        targetStr: String,
        coverImage: String,
        collaboratorIds: List<String>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val target = targetStr.toDoubleOrNull() ?: 0.0
            val initialBalance = currentBalanceStr.toDoubleOrNull() ?: 0.0
            runCatching {
                val created = boxRepository.addBox(
                    name = name,
                    target = target,
                    coverImage = coverImage,
                    collaborators = collaboratorIds
                )
                // Backend always starts boxes at $0; if the user entered an initial
                // balance, materialize it as an immediate deposit.
                if (initialBalance > 0.0) {
                    boxRepository.deposit(
                        boxId = created.id,
                        title = "Initial deposit",
                        amount = initialBalance,
                        author = SampleData.currentUser
                    )
                }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "Couldn't create the box") }
            }
        }
    }

    fun deposit(boxId: String, title: String, amount: Double) {
        viewModelScope.launch {
            runCatching {
                boxRepository.deposit(boxId, title, amount, SampleData.currentUser)
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "Deposit failed") }
            }
        }
    }

    fun withdraw(boxId: String, title: String, amount: Double, note: String?) {
        viewModelScope.launch {
            runCatching {
                boxRepository.withdraw(boxId, title, amount, note, SampleData.currentUser)
            }.onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.message ?: "Withdraw failed") }
            }
        }
    }

    fun postComment(boxId: String, transactionId: String, text: String) {
        if (text.isBlank()) return
        boxRepository.postComment(boxId, transactionId, text, SampleData.currentUser)
    }

    class Factory(
        private val repository: TreasureBoxRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass.isAssignableFrom(MainViewModel::class.java))
            return MainViewModel(repository) as T
        }
    }
}
