package com.example.smartdailyexpensetracker.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: ExpenseCategory,
    val notes: String = "",
    val receiptImagePath: String = "",
    val dateTime: LocalDateTime = LocalDateTime.now()
) {
    val formattedDate: String
        get() = dateTime.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

    val formattedTime: String
        get() = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
}


enum class ExpenseCategory(val displayName: String, val emoji: String) {
    STAFF("Staff", "👥"),
    TRAVEL("Travel", "🚗"),
    FOOD("Food", "🍽️"),
    UTILITY("Utility", "⚡")
}

data class ExpenseEntryUiState(
    val title: String = "",
    val amount: String = "",
    val selectedCategory: ExpenseCategory = ExpenseCategory.FOOD,
    val notes: String = "",
    val receiptImagePath: String = "",
    val isLoading: Boolean = false
)

enum class GroupBy { TIME, CATEGORY }
