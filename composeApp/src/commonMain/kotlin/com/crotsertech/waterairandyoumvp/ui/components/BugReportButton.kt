package com.crotsertech.waterairandyoumvp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.platform.getDeviceInfo
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.theme.glow

private const val GITHUB_ISSUES_URL = "https://github.com/Juls-by-Strong/Water-Air-and-You/issues/new"
private const val SUPPORT_EMAIL = "info@waterairandyou.com"
private const val CC_EMAIL = "ntc@crotser.dev"

@Composable
fun BugReportButton(
    modifier: Modifier = Modifier,
    errorContext: String = ""
) {
    var showDialog by remember { mutableStateOf(false) }
    val openUrl = rememberUrlOpener()
    val deviceInfo = remember { getDeviceInfo() }

    Box(modifier = modifier.padding(vertical = 4.dp).glow(), contentAlignment = Alignment.Center) {
        OutlinedButton(
            onClick = { showDialog = true },
            modifier = Modifier
        ) {
            Text("Report a Bug", fontSize = 12.sp)
        }
    }

    if (showDialog) {
        BugReportChoiceDialog(
            deviceInfo = deviceInfo,
            errorContext = errorContext,
            onDismiss = { showDialog = false },
            onOpenUrl = openUrl
        )
    }
}

@Composable
private fun BugReportChoiceDialog(
    deviceInfo: String,
    errorContext: String,
    onDismiss: () -> Unit,
    onOpenUrl: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Report a Bug")
        },
        text = {
            Text(
                text = "We prefer bug reports via GitHub Issues - it helps us track and resolve problems faster. " +
                       "If you don't use GitHub, you can send us an email instead.",
                fontSize = 13.sp
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onOpenUrl(buildGitHubUrl(deviceInfo, errorContext))
                onDismiss()
            }) {
                Text("Report via GitHub (preferred)")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onOpenUrl(buildEmailUrl(deviceInfo, errorContext))
                onDismiss()
            }) {
                Text("Send via Email")
            }
        }
    )
}

private fun buildGitHubUrl(deviceInfo: String, errorContext: String): String {
    val body = buildString {
        appendLine("**Describe the bug:**")
        appendLine()
        appendLine()
        appendLine("**Steps to reproduce:**")
        appendLine("1.")
        appendLine("2.")
        appendLine("3.")
        appendLine()
        appendLine("**Expected behavior:**")
        appendLine()
        appendLine()
        appendLine("**Device info:**")
        deviceInfo.lines().forEach { appendLine("- $it") }
        if (errorContext.isNotBlank()) {
            appendLine()
            appendLine("**Error:**")
            appendLine(errorContext)
        }
    }
    return "$GITHUB_ISSUES_URL?labels=bug&title=Bug%20Report&body=${urlEncoded(body)}"
}

private fun buildEmailUrl(deviceInfo: String, errorContext: String): String {
    val body = buildString {
        appendLine("Describe what happened:")
        appendLine()
        appendLine()
        appendLine("Steps to reproduce:")
        appendLine("1.")
        appendLine("2.")
        appendLine("3.")
        appendLine()
        appendLine("Expected behavior:")
        appendLine()
        appendLine()
        appendLine("Device info:")
        appendLine(deviceInfo)
        if (errorContext.isNotBlank()) {
            appendLine()
            appendLine("Error:")
            appendLine(errorContext)
        }
    }
    val subject = "Bug%20Report"
    return "mailto:$SUPPORT_EMAIL?cc=$CC_EMAIL&subject=$subject&body=${urlEncoded(body)}"
}

private fun urlEncoded(text: String): String = buildString {
    for (c in text) {
        when {
            c in '0'..'9' || c in 'A'..'Z' || c in 'a'..'z' || c == '-' || c == '_' || c == '.' || c == '~' -> append(c)
            c == ' ' -> append("%20")
            c == '\n' -> append("%0A")
            c == '\r' -> append("%0D")
            c == '\t' -> append("%09")
            else -> {
                val hex = c.code.toString(16).uppercase()
                if (hex.length == 1) append("%0$hex") else append("%$hex")
            }
        }
    }
}
