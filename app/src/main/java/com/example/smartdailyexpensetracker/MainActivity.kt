package com.example.smartdailyexpensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartdailyexpensetracker.composble_screen.ExpenseEntryScreen
import com.example.smartdailyexpensetracker.composble_screen.ExpenseListScreen
import com.example.smartdailyexpensetracker.composble_screen.ExpenseReportScreen
import com.example.smartdailyexpensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                ExpenseTrackerApp()
            }
        }
    }
}

@Composable
fun ExpenseTrackerApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "expense_list"
    ) {
        composable("expense_list") {
            ExpenseListScreen(
                onNavigateToEntry = { navController.navigate("expense_entry") },
                onNavigateToReports = { navController.navigate("expense_reports") }
            )
        }

        composable("expense_entry") {
            ExpenseEntryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("expense_reports") {
            ExpenseReportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
