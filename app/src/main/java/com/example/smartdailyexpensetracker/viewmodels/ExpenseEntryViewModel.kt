package com.example.smartdailyexpensetracker.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdailyexpensetracker.data_classes.Expense
import com.example.smartdailyexpensetracker.data_classes.ExpenseCategory
import com.example.smartdailyexpensetracker.data_classes.ExpenseEntryUiState
import com.example.smartdailyexpensetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseEntryViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseEntryUiState())
    val uiState: StateFlow<ExpenseEntryUiState> = _uiState.asStateFlow()

    private val _todayTotal = MutableStateFlow(0.0)
    val todayTotal: StateFlow<Double> = _todayTotal.asStateFlow()

    init {
        loadTodayTotal()
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateCategory(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun updateNotes(notes: String) {
        if (notes.length <= 100) {
            _uiState.value = _uiState.value.copy(notes = notes)
        }
    }
    fun updateReceiptImage(imagePath: String) {
        _uiState.value = _uiState.value.copy(receiptImagePath = imagePath)
    }

    fun removeReceiptImage() {
        _uiState.value = _uiState.value.copy(receiptImagePath = "")
    }

    fun addExpense(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value

        // Validation
        if (state.title.isBlank()) {
            onError("Title cannot be empty")
            return
        }

        val amountValue = state.amount.toDoubleOrNull()
        if (amountValue == null || amountValue <= 0) {
            onError("Amount must be greater than 0")
            return
        }

        val expense = Expense(
            title = state.title.trim(),
            amount = amountValue,
            category = state.selectedCategory,
            notes = state.notes.trim(),
            receiptImagePath = state.receiptImagePath
        )

        viewModelScope.launch {
            try {
                repository.addExpense(expense)
                clearForm()
                loadTodayTotal()
                onSuccess()
            } catch (e: Exception) {
                onError("Failed to add expense: ${e.message}")
            }
        }
    }

    private fun clearForm() {
        _uiState.value = ExpenseEntryUiState()
    }

    private fun loadTodayTotal() {
        viewModelScope.launch {
            val total = repository.getTotalForDate(LocalDate.now())
            _todayTotal.value = total
        }
    }
}