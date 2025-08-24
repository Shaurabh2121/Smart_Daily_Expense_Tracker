package com.example.smartdailyexpensetracker.repository

import com.example.smartdailyexpensetracker.db.dao.ExpenseDao
import com.example.smartdailyexpensetracker.data_classes.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val expenseDao: ExpenseDao
) {
    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getExpensesByDate(date: LocalDate): Flow<List<Expense>> {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return expenseDao.getExpensesByDate(dateString)
    }

    fun getExpensesByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        val startString = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endString = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return expenseDao.getExpensesByDateRange(startString, endString)
    }

    suspend fun getTotalForDate(date: LocalDate): Double {
        val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return expenseDao.getTotalForDate(dateString) ?: 0.0
    }

    suspend fun addExpense(expense: Expense): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
