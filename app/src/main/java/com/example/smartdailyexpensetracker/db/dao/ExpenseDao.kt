package com.example.smartdailyexpensetracker.db.dao

import androidx.room.*
import com.example.smartdailyexpensetracker.data_classes.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY dateTime DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE DATE(dateTime) = :date ORDER BY dateTime DESC")
    fun getExpensesByDate(date: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE DATE(dateTime) BETWEEN :startDate AND :endDate ORDER BY dateTime DESC")
    fun getExpensesByDateRange(startDate: String, endDate: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE DATE(dateTime) = :date")
    suspend fun getTotalForDate(date: String): Double?

    @Insert
    suspend fun insertExpense(expense: Expense): Long

    @Delete
    suspend fun deleteExpense(expense: Expense)
}