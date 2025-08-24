package com.example.smartdailyexpensetracker.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartdailyexpensetracker.data_classes.Expense
import com.example.smartdailyexpensetracker.data_classes.ExpenseCategory
import com.example.smartdailyexpensetracker.repository.ExpenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ExpenseReportViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val endDate = LocalDate.now()
    private val startDate = endDate.minusDays(6)

    val weeklyExpenses = repository.getExpensesByDateRange(startDate, endDate)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val dailyTotals = weeklyExpenses.map { expenses ->
        expenses.groupBy { it.dateTime.toLocalDate() }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }
            .toSortedMap()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val categoryTotals = weeklyExpenses.map { expenses ->
        expenses.groupBy { it.category }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyMap()
    )

    val totalWeeklyAmount = weeklyExpenses.map { list ->
        list.sumOf { it.amount }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )


    // Export Functions
    fun exportToPDF(
        context: Context,
        expenses: List<Expense>,
        dailyTotals: Map<java.time.LocalDate, Double>,
        categoryTotals: Map<ExpenseCategory, Double>,
        totalAmount: Double
    ) {
        val pdfContent = buildString {
            appendLine("EXPENSE REPORT - LAST 7 DAYS")
            appendLine("Generated on: ${java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
            appendLine("=".repeat(50))
            appendLine()

            appendLine("SUMMARY:")
            appendLine("Total Amount: ₹${String.format("%.2f", totalAmount)}")
            appendLine("Total Expenses: ${expenses.size}")
            appendLine()

            appendLine("DAILY BREAKDOWN:")
            dailyTotals.toSortedMap().forEach { (date, total) ->
                appendLine("${date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))}: ₹${String.format("%.2f", total)}")
            }
            appendLine()

            appendLine("CATEGORY BREAKDOWN:")
            categoryTotals.forEach { (category, total) ->
                val percentage = if (totalAmount > 0) (total / totalAmount * 100) else 0.0
                appendLine("${category.displayName}: ₹${String.format("%.2f", total)} (${String.format("%.1f", percentage)}%)")
            }
            appendLine()

            appendLine("DETAILED EXPENSES:")
            expenses.sortedByDescending { it.dateTime }.forEach { expense ->
                appendLine("${expense.formattedDate} ${expense.formattedTime} - ${expense.title}")
                appendLine("  Amount: ₹${String.format("%.2f", expense.amount)} | Category: ${expense.category.displayName}")
                if (expense.notes.isNotEmpty()) {
                    appendLine("  Notes: ${expense.notes}")
                }
                appendLine()
            }
        }

        // Simulate PDF creation and trigger share
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, pdfContent)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Report - Last 7 Days")
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share Expense Report"))
        } catch (e: Exception) {
            // Handle error - in a real app, show a Toast or Snackbar
        }
    }


    fun exportToCSV(context: Context, expenses: List<Expense>) {
        val csvContent = buildString {
            // CSV Header
            appendLine("Date,Time,Title,Amount,Category,Notes")

            // CSV Data
            expenses.sortedByDescending { it.dateTime }.forEach { expense ->
                val csvRow = listOf(
                    expense.formattedDate,
                    expense.formattedTime,
                    "\"${expense.title}\"", // Quotes for text with potential commas
                    expense.amount.toString(),
                    expense.category.displayName,
                    "\"${expense.notes}\"" // Quotes for notes
                ).joinToString(",")
                appendLine(csvRow)
            }
        }

        // Trigger share intent for CSV
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/csv"
            putExtra(Intent.EXTRA_TEXT, csvContent)
            putExtra(Intent.EXTRA_SUBJECT, "Expense Data - CSV Export")
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share CSV Data"))
        } catch (e: Exception) {
            // Handle error - in a real app, show a Toast or Snackbar
        }
    }

    fun shareReport(
        context: Context,
        expenses: List<Expense>,
        dailyTotals: Map<java.time.LocalDate, Double>,
        categoryTotals: Map<ExpenseCategory, Double>,
        totalAmount: Double
    ) {
        val reportSummary = buildString {
            appendLine("📊 EXPENSE SUMMARY - LAST 7 DAYS")
            appendLine()
            appendLine("💰 Total Spent: ₹${String.format("%.2f", totalAmount)}")
            appendLine("📝 Total Expenses: ${expenses.size}")
            appendLine()
            appendLine("📅 Top Spending Days:")
            dailyTotals.toList().sortedByDescending { it.second }.take(3).forEach { (date, total) ->
                appendLine("• ${date.format(java.time.format.DateTimeFormatter.ofPattern("EEE, MMM dd"))}: ₹${String.format("%.2f", total)}")
            }
            appendLine()
            appendLine("🏷️ Category Breakdown:")
            categoryTotals.toList().sortedByDescending { it.second }.forEach { (category, total) ->
                val percentage = if (totalAmount > 0) (total / totalAmount * 100) else 0.0
                appendLine("• ${category.emoji} ${category.displayName}: ₹${String.format("%.2f", total)} (${String.format("%.1f", percentage)}%)")
            }
            appendLine()
            appendLine("Generated by Smart Expense Tracker 📱")
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, reportSummary)
            putExtra(Intent.EXTRA_SUBJECT, "My Weekly Expense Report")
        }

        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share Report"))
        } catch (e: Exception) {
            // Handle error - in a real app, show a Toast or Snackbar
        }
    }

}