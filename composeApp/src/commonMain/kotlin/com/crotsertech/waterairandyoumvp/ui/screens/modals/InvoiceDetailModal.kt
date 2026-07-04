package com.crotsertech.waterairandyoumvp.ui.screens.modals

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.api.TokenRepository
import com.crotsertech.waterairandyoumvp.data.model.Invoice
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.ui.components.ToastManager
import com.crotsertech.waterairandyoumvp.ui.components.ToastType
import com.crotsertech.waterairandyoumvp.ui.components.WayModal
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun InvoiceDetailModal(
    visible: Boolean,
    invoiceId: Int,
    scope: CoroutineScope,
    onDismiss: () -> Unit,
    onPaymentInitiated: (payUrl: String) -> Unit
) {
    var invoice by remember { mutableStateOf<Invoice?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var retryTrigger by remember { mutableStateOf(0) }
    var paying by remember { mutableStateOf(false) }

    LaunchedEffect(visible, invoiceId, retryTrigger) {
        if (visible) {
            isLoading = true
            errorMsg = null
            withContext(Dispatchers.Default) {
                ApiService.getInvoice(invoiceId)
            }.onSuccess {
                invoice = it
                isLoading = false
            }.onFailure {
                errorMsg = it.message ?: "Failed to load invoice"
                isLoading = false
            }
        }
    }

    WayModal(
        visible = visible,
        onDismiss = onDismiss,
        title = "Invoice Detail",
        subtitle = invoice?.invoice_number ?: ""
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@WayModal
        }

        val inv = invoice ?: run {
            Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(errorMsg ?: "Failed to load invoice", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(onClick = { retryTrigger++ }) {
                        Text("Retry")
                    }
                }
            }
            return@WayModal
        }

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Issue date:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(inv.issue_date, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Status:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    val statusColor = when (inv.status) {
                        "paid" -> MaterialTheme.colorScheme.primary
                        "overdue" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.tertiary
                    }
                    Text(inv.status.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = statusColor)
                }
                Spacer(Modifier.height(8.dp))
                val openUrl = rememberUrlOpener()
                OutlinedButton(
                    onClick = {
                        val url = "${ApiService.BASE_URL}customer/invoices/${inv.invoice_id}/pdf?token=${TokenRepository.accessToken}"
                        openUrl(url)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Invoice PDF")
                }
            }
        }

        val invoiceLines = inv.lineItems.orEmpty()
        if (invoiceLines.isNotEmpty()) {
            Text("Line Items", fontSize = 13.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("Item", modifier = Modifier.weight(2f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Qty", modifier = Modifier.weight(0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        Text("Price", modifier = Modifier.weight(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                        Text("Total", modifier = Modifier.weight(0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    invoiceLines.forEach { line ->
                        Row(Modifier.fillMaxWidth()) {
                            Text(line.description, modifier = Modifier.weight(2f), fontSize = 12.sp)
                            Text("${line.quantity}", modifier = Modifier.weight(0.5f), fontSize = 12.sp, textAlign = TextAlign.Center)
                            Text("$${line.unit_price.toFixed(2)}", modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.End)
                            Text("$${line.total.toFixed(2)}", modifier = Modifier.weight(0.8f), fontSize = 12.sp, textAlign = TextAlign.End)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(Modifier.fillMaxWidth()) {
                        Text("Total", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("$${inv.total.toFixed(2)}", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }
                    if (inv.amount_paid != null && inv.amount_paid!! > 0.005) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Amount paid", modifier = Modifier.weight(1f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            Text("$${inv.amount_paid!!.toFixed(2)}", modifier = Modifier.weight(1f), fontSize = 13.sp, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.End)
                        }
                    }
                    if (inv.balance > 0.005) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Balance due", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text("$${inv.balance.toFixed(2)}", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.End)
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Invoice total: $${inv.total.toFixed(2)}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (inv.isPayable) {
            val feeAmount = inv.total * 0.035
            Surface(
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Card payments include a 3.5% processing fee ($${feeAmount.toFixed(2)}). Total with fee: $${(inv.total + feeAmount).toFixed(2)}.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Button(
                onClick = {
                    paying = true
                    scope.launch {
                        val result = ApiService.initiatePayment(inv.invoice_id)
                        paying = false
                        result.onSuccess { payment ->
                            onPaymentInitiated(payment.pay_url)
                            onDismiss()
                        }.onFailure { e ->
                            ToastManager.show(e.message ?: "Payment failed to initiate", ToastType.Error)
                        }
                    }
                },
                enabled = !paying,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (paying) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                } else {
                    Text("Pay Now \u2014 $${inv.balance.toFixed(2)}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun Double.toFixed(digits: Int): String {
    val factor = when (digits) { 0 -> 1; 1 -> 10; 2 -> 100; 3 -> 1000; else -> 1 }
    val scaled = (this * factor).toLong()
    val whole = scaled / factor
    val fraction = (scaled % factor).let { if (it < 0) -it else it }
    return if (digits > 0) "$whole." + fraction.toString().padStart(digits, '0') else whole.toString()
}
