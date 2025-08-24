package com.example.smartdailyexpensetracker.composble_screen


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.smartdailyexpensetracker.R
import com.example.smartdailyexpensetracker.viewmodels.ExpenseReportViewModel
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseReportScreen(
    onNavigateBack: () -> Unit,
    viewModel: ExpenseReportViewModel = hiltViewModel()
) {
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val categoryTotals by viewModel.categoryTotals.collectAsState()
    val totalWeeklyAmount by viewModel.totalWeeklyAmount.collectAsState()
    val weeklyExpenses by viewModel.weeklyExpenses.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Expense Reports") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = {
                    viewModel.shareReport(context, weeklyExpenses, dailyTotals, categoryTotals, totalWeeklyAmount)
                }) {
                    Icon(Icons.Default.Share, contentDescription = "Export")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Last 7 Days Total",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "₹${String.format("%.2f", totalWeeklyAmount)}",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Daily Totals Chart
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily Spending",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        SimpleBarChart(
                            data = dailyTotals,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            // Daily Totals Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Daily Totals",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }

            items(dailyTotals.toList().sortedBy { it.first }) { (date, total) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = date.format(DateTimeFormatter.ofPattern("EEE, MMM dd")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "₹${String.format("%.2f", total)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Category Breakdown
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Category Breakdown",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }

            items(categoryTotals.toList()) { (category, total) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = category.emoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = category.displayName,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${String.format("%.2f", total)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val percentage = if (totalWeeklyAmount > 0) (total / totalWeeklyAmount * 100) else 0.0
                        Text(
                            text = "${String.format("%.1f", percentage)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    val percentage = if (totalWeeklyAmount > 0) (total / totalWeeklyAmount).toFloat() else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(percentage)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Export Options
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Export Options",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                viewModel.exportToPDF(context, weeklyExpenses, dailyTotals, categoryTotals, totalWeeklyAmount)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.file_pdf),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export PDF")
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.exportToCSV(context, weeklyExpenses)
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.chart_simple),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export CSV")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleBarChart(
    data: Map<java.time.LocalDate, Double>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier.padding(16.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (data.isEmpty()) return@Canvas

        val maxValue = data.values.maxOrNull() ?: 1.0
        val barWidth = canvasWidth / data.size

        data.values.forEachIndexed { index, value ->
            val barHeight = (value / maxValue * canvasHeight * 0.8).toFloat()
            val x = index * barWidth + barWidth * 0.1f
            val y = canvasHeight - barHeight

            drawRect(
                color = primaryColor,
                topLeft = Offset(x, y),
                size = androidx.compose.ui.geometry.Size(barWidth * 0.8f, barHeight)
            )
        }
    }
}