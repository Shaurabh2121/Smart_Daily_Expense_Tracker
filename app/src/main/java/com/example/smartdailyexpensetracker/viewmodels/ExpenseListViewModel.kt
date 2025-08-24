package com.example.smartdailyexpensetracker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdailyexpensetracker.data_classes.Expense
import com.example.smartdailyexpensetracker.data_classes.GroupBy
import com.example.smartdailyexpensetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseListViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _groupBy = MutableStateFlow(GroupBy.TIME)
    val groupBy: StateFlow<GroupBy> = _groupBy.asStateFlow()

    val expenses = _selectedDate.flatMapLatest { date ->
        repository.getExpensesByDate(date)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val groupedExpenses = combine(expenses, groupBy) { expenseList, grouping ->
        when (grouping) {
            GroupBy.CATEGORY -> expenseList.groupBy { it.category }
            GroupBy.TIME -> expenseList.groupBy { it.formattedDate }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val totalAmount = expenses.map { list ->
        list.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleGroupBy() {
        _groupBy.value = if (_groupBy.value == GroupBy.TIME) GroupBy.CATEGORY else GroupBy.TIME
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}