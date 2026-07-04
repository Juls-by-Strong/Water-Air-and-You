package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.data.model.Invoice
import com.crotsertech.waterairandyoumvp.ui.viewmodel.InvoicesUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.InvoicesViewModel

@Composable
fun InvoicesScreen(
    onInvoiceClick: (Int) -> Unit = {},
    viewModel: InvoicesViewModel = viewModel { InvoicesViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    var filter by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Billing",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Invoices, payments, and balance",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(selected = filter == "", label = "All", onClick = { filter = "" })
            FilterChip(selected = filter == "sent", label = "Outstanding", onClick = { filter = "sent" })
            FilterChip(selected = filter == "paid", label = "Paid", onClick = { filter = "paid" })
        }
        Spacer(Modifier.height(8.dp))

        when (val state = uiState) {
            is InvoicesUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is InvoicesUiState.Success -> {
                val filtered = if (filter.isBlank()) state.invoices
                else state.invoices.filter { it.status == filter }

                if (filtered.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No invoices found")
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filtered) { item ->
                            InvoiceCard(item, onClick = { onInvoiceClick(item.invoice_id) })
                        }
                    }
                }
            }
            is InvoicesUiState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadInvoices() }) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text(label, fontSize = 13.sp)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
            Text(label, fontSize = 13.sp)
        }
    }
}

@Composable
private fun InvoiceCard(inv: Invoice, onClick: () -> Unit = {}) {
    val statusColor = when (inv.status) {
        "paid" -> MaterialTheme.colorScheme.primary
        "overdue" -> MaterialTheme.colorScheme.error
        "sent", "partial" -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(inv.invoice_number, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(inv.issue_date, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${inv.total.toFixed(2)}", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = inv.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight(700),
                        letterSpacing = 0.3.sp,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

private fun Double.toFixed(digits: Int): String {
    val factor = when (digits) {
        0 -> 1; 1 -> 10; 2 -> 100; 3 -> 1000
        else -> 1
    }
    val scaled = (this * factor).toLong()
    val whole = scaled / factor
    val fraction = (scaled % factor).let { if (it < 0) -it else it }
    return if (digits > 0) "$whole." + fraction.toString().padStart(digits, '0') else whole.toString()
}
