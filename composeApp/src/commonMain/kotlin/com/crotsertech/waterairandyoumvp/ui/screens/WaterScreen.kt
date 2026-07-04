package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.api.TokenRepository
import com.crotsertech.waterairandyoumvp.data.model.WaterTest
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.ui.viewmodel.WaterUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.WaterViewModel

@Composable
fun WaterScreen(
    viewModel: WaterViewModel = viewModel { WaterViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Water Testing",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your water test results",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        when (val state = uiState) {
            is WaterUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is WaterUiState.Success -> {
                if (state.tests.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No water tests found")
                    }
                } else {
                    val openUrl = rememberUrlOpener()
                    val current = state.tests.firstOrNull { it.is_current }
                    val history = state.tests.filter { !it.is_current }

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (current != null) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                                ) {
                                    Column(Modifier.padding(16.dp)) {
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text("Current Report", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                            Surface(
                                                shape = MaterialTheme.shapes.small,
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    "CURRENT",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight(700),
                                                    letterSpacing = 0.3.sp,
                                                    color = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                        Spacer(Modifier.height(6.dp))
                                        Text(current.label, fontSize = 14.sp)
                                        Text("Date: ${current.test_date}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(Modifier.height(8.dp))
                                        Button(
                                            onClick = {
                                                val url = "${ApiService.BASE_URL}water_test.php?test_id=${current.test_id}&token=${TokenRepository.accessToken}"
                                                openUrl(url)
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("Open Report PDF")
                                        }
                                    }
                                }
                            }
                        }

                        if (history.isNotEmpty()) {
                            item {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "ALL REPORTS".uppercase(),
                                    fontSize = 11.sp,
                                    letterSpacing = 1.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight(600)
                                )
                            }
                            items(history) { test ->
                                WaterTestCard(test)
                            }
                        }
                    }
                }
            }
            is WaterUiState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadWaterTests() }) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun WaterTestCard(test: WaterTest) {
    val openUrl = rememberUrlOpener()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(test.label, fontWeight = FontWeight(600), fontSize = 15.sp)
                Text(test.test_date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(
                onClick = {
                    val url = "${ApiService.BASE_URL}water_test.php?test_id=${test.test_id}&token=${TokenRepository.accessToken}"
                    openUrl(url)
                }
            ) {
                Text("PDF", fontSize = 12.sp)
            }
        }
    }
}
