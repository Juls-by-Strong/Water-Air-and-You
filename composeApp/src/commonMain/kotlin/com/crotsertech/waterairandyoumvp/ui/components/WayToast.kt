package com.crotsertech.waterairandyoumvp.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ToastType { Success, Error, Info, Warning }

data class ToastMessage(
    val id: Long,
    val text: String,
    val type: ToastType = ToastType.Info
)

object ToastManager {
    private val _messages = MutableStateFlow<List<ToastMessage>>(emptyList())
    val messages = _messages.asStateFlow()
    private var nextId = 0L

    fun show(text: String, type: ToastType = ToastType.Info) {
        val msg = ToastMessage(nextId++, text, type)
        _messages.value = _messages.value + msg
    }

    fun dismiss(id: Long) {
        _messages.value = _messages.value.filter { it.id != id }
    }
}

@Composable
fun ToastHost() {
    val messages by ToastManager.messages.collectAsState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            delay(3000)
            ToastManager.dismiss(messages.first().id)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier.padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            messages.forEach { msg ->
                ToastItem(msg, onDismiss = { ToastManager.dismiss(msg.id) })
            }
        }
    }
}

@Composable
private fun ToastItem(msg: ToastMessage, onDismiss: () -> Unit) {
    val surfaceColor = when (msg.type) {
        ToastType.Success -> Color(0xFF059669)
        ToastType.Error -> Color(0xFFDC2626)
        ToastType.Info -> Color(0xFF2563EB)
        ToastType.Warning -> Color(0xFFD97706)
    }

    val icon = when (msg.type) {
        ToastType.Success -> "\u2713"
        ToastType.Error -> "\u2717"
        ToastType.Info -> "\u24D8"
        ToastType.Warning -> "\u26A0"
    }

    Surface(
        modifier = Modifier.padding(horizontal = 16.dp).clickable(onClick = onDismiss),
        shape = RoundedCornerShape(12.dp),
        color = surfaceColor,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 18.sp, color = Color.White)
            Spacer(Modifier.width(10.dp))
            Text(
                text = msg.text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
