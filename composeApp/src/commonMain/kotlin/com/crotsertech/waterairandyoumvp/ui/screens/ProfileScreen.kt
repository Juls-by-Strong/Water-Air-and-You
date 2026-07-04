package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.crotsertech.waterairandyoumvp.theme.LocalThemeState
import com.crotsertech.waterairandyoumvp.ui.components.BugReportButton
import com.crotsertech.waterairandyoumvp.ui.components.ToastManager
import com.crotsertech.waterairandyoumvp.ui.components.ToastType
import com.crotsertech.waterairandyoumvp.ui.viewmodel.ProfileUiState
import com.crotsertech.waterairandyoumvp.ui.viewmodel.ProfileViewModel
import com.crotsertech.waterairandyoumvp.APP_VERSION
import com.crotsertech.waterairandyoumvp.platform.rememberUrlOpener
import com.crotsertech.waterairandyoumvp.platform.showLocalNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel { ProfileViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val passwordResult by viewModel.passwordResult.collectAsState()
    val pushSubscribed by viewModel.pushSubscribed.collectAsState()
    var pwCurrent by remember { mutableStateOf("") }
    var pwNew by remember { mutableStateOf("") }
    var pwConfirm by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    LaunchedEffect(passwordResult) {
        passwordResult?.let { msg ->
            val isSuccess = msg == "Password changed successfully"
            ToastManager.show(msg, if (isSuccess) ToastType.Success else ToastType.Error)
            viewModel.clearPasswordResult()
            if (isSuccess) { pwCurrent = ""; pwNew = ""; pwConfirm = "" }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Your account & preferences",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ProfileUiState.Success -> {
                val c = state.customer
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Contact Information", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(8.dp))
                        Text("${c.first_name ?: ""} ${c.last_name ?: ""}".trim(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        c.email?.let { Text(it, fontSize = 14.sp) }
                        c.phone?.let { Text(it, fontSize = 14.sp) }
                        if (!c.address.isNullOrBlank()) {
                            Text(
                                listOfNotNull(c.address, c.city, c.state, c.zip).filter { it.isNotBlank() }.joinToString(", "),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        ContactUsBox()
                    }
                }

                ChangePasswordCard(
                    current = pwCurrent,
                    onCurrentChange = { pwCurrent = it },
                    newPw = pwNew,
                    onNewChange = { pwNew = it },
                    confirm = pwConfirm,
                    onConfirmChange = { pwConfirm = it },
                    onSubmit = {
                        if (pwNew.length < 6) {
                            ToastManager.show("Password must be at least 6 characters", ToastType.Error)
                        } else if (pwNew != pwConfirm) {
                            ToastManager.show("Passwords do not match", ToastType.Error)
                        } else {
                            viewModel.changePassword(pwCurrent, pwNew)
                        }
                    }
                )

                ThemePickerCard()

                PushNotificationCard(
                    enabled = pushSubscribed,
                    onToggle = { viewModel.togglePushSubscription(it) }
                )

                TestNotificationCard()

                ContactCard()

                FeedbackCard()

                SignOutCard(onLogout, viewModel)

                Spacer(Modifier.height(8.dp))
                Text(
                    text = APP_VERSION,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )
            }
            is ProfileUiState.Error -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadProfile() }) { Text("Retry") }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(24.dp)) // Extra space at bottom for scrolling
    }
}

@Composable
private fun ContactUsBox() {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "\uD83D\uDCDE To update your address or contact info, please call or email us:\n\u260E 309-643-1342 \u00B7 \u2709 info@waterairandyou.com",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
private fun ChangePasswordCard(
    current: String, onCurrentChange: (String) -> Unit,
    newPw: String, onNewChange: (String) -> Unit,
    confirm: String, onConfirmChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Change Password", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = current, onValueChange = onCurrentChange, label = { Text("Current Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = newPw, onValueChange = onNewChange, label = { Text("New Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = confirm, onValueChange = onConfirmChange, label = { Text("Confirm New Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Update Password") }
        }
    }
}

@Composable
private fun ThemePickerCard() {
    val themeState = LocalThemeState.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Appearance", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("Choose your theme", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeButton(emoji = "🌙", label = "SNEP Dark", desc = "Easy on the eyes", isActive = !themeState.useMetro && themeState.useDark, isMetro = false, isDark = true, modifier = Modifier.weight(1f))
                    ThemeButton(emoji = "☀️", label = "SNEP Light", desc = "Warm & bright", isActive = !themeState.useMetro && !themeState.useDark, isMetro = false, isDark = false, modifier = Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeButton(emoji = "🌆", label = "METRO Dark", desc = "Sleek & sharp", isActive = themeState.useMetro && themeState.useDark, isMetro = true, isDark = true, modifier = Modifier.weight(1f))
                    ThemeButton(emoji = "🏙️", label = "METRO Light", desc = "Clean & minimal", isActive = themeState.useMetro && !themeState.useDark, isMetro = true, isDark = false, modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ThemeButton(emoji: String, label: String, desc: String, isActive: Boolean, isMetro: Boolean, isDark: Boolean, modifier: Modifier = Modifier) {
    val onThemeChanged = LocalThemeState.current.onThemeChanged
    val shape = MaterialTheme.shapes.small
    val button = @Composable { modifier: Modifier ->
        if (isActive) {
            FilledTonalButton(onClick = { }, modifier = modifier, shape = shape) {
                Text("$emoji  $label", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            OutlinedButton(onClick = { onThemeChanged(isMetro, isDark) }, modifier = modifier, shape = shape) {
                Text("$emoji  $label", fontSize = 12.sp)
            }
        }
    }
    Column(modifier = modifier) {
        button(Modifier.fillMaxWidth())
        Text(desc, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp, start = 4.dp))
    }
}

@Composable
private fun PushNotificationCard(enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Notifications", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Enable push notifications", fontWeight = FontWeight(600), fontSize = 14.sp)
                    Text("We'll let you know when your visit is confirmed and when service is finished.",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = enabled, onCheckedChange = { onToggle(!enabled) })
            }
        }
    }
}

@Composable
private fun TestNotificationCard() {
    val scope = rememberCoroutineScope()
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Test Notification", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text("Send a test notification to verify notifications are working", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    scope.launch {
                        ToastManager.show("Test notification in 10 seconds...", ToastType.Info)
                        delay(10_000)
                        showLocalNotification("Test Notification", "This is a test notification from Water Air and You")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Test Notification")
            }
        }
    }
}

@Composable
private fun ContactCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Contact us", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Phone", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                Text("309-643-1342", fontWeight = FontWeight(500), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Email", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                Text("info@waterairandyou.com", fontWeight = FontWeight(500), fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun FeedbackCard() {
    val openUrl = rememberUrlOpener()
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Feedback", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BugReportButton(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = { openUrl("https://github.com/Juls-by-Strong/Water-Air-and-You/issues/new?labels=enhancement") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Request a Feature", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SignOutCard(onLogout: () -> Unit, viewModel: ProfileViewModel) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Account", fontSize = 11.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight(600), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Sign Out", fontWeight = FontWeight.Medium)
            }
        }
    }
}
