package com.crotsertech.waterairandyoumvp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crotsertech.waterairandyoumvp.data.api.ApiService
import com.crotsertech.waterairandyoumvp.data.api.TokenRepository
import com.crotsertech.waterairandyoumvp.theme.WayTheme
import com.crotsertech.waterairandyoumvp.APP_VERSION
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import waterairandyou.composeapp.generated.resources.Res
import waterairandyou.composeapp.generated.resources.ntsky

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val api = ApiService

    LaunchedEffect(Unit) {
        val saved = TokenRepository.savedEmail
        if (saved.isNotBlank()) {
            email = saved
            password = TokenRepository.savedPassword
            rememberMe = true
        }
    }

    val isValid = email.isNotBlank() && password.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.ntsky),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Water Air & You",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "MVP - Your Most Valuable Partner in your Home Health!",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
            }

            val loginTextColor = Color.White
            val loginDimColor = loginTextColor.copy(alpha = 0.5f)
            val loginDimmerColor = loginTextColor.copy(alpha = 0.3f)

            if (WayTheme.colors.isMetro) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xCC1F1F1F))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text("Email", color = loginDimColor) },
                            placeholder = { Text("you@example.com", color = loginDimmerColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = loginTextColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = loginDimColor,
                                unfocusedBorderColor = loginDimmerColor,
                                cursorColor = loginTextColor,
                                focusedLabelColor = loginDimColor,
                                unfocusedLabelColor = loginDimmerColor
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text("Password", color = loginDimColor) },
                            placeholder = { Text("••••••••", color = loginDimmerColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = loginTextColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = loginDimColor,
                                unfocusedBorderColor = loginDimmerColor,
                                cursorColor = loginTextColor,
                                focusedLabelColor = loginDimColor,
                                unfocusedLabelColor = loginDimmerColor
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = loginTextColor, uncheckedColor = loginDimColor, checkmarkColor = Color.Black))
                            Text("Remember me", fontSize = 13.sp, color = loginDimColor)
                        }
                        errorMessage?.let { msg ->
                            Text(text = msg, fontSize = 13.sp, color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp), textAlign = TextAlign.Center)
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {
                            if (isValid && !isLoading) {
                                isLoading = true; errorMessage = null
                                scope.launch {
                                    api.login(email, password).also { isLoading = false }
                                        .onSuccess {
                                            if (rememberMe) TokenRepository.saveCredentials(email, password)
                                            else TokenRepository.clearCredentials()
                                            onLoginSuccess()
                                        }.onFailure { 
                                            println("LOGIN_ERROR: ${it.message}")
                                            errorMessage = it.message ?: "Login failed. Please try again." 
                                        }
                                }
                            }
                        }, enabled = isValid && !isLoading,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(0.dp)) {
                            Text(if (isLoading) "Signing in…" else "Sign In", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            } else {
                val colors = WayTheme.colors
                val cardRadiusDp = colors.radiusLg.dp
                // SNEP - Glass-morphic gel card with inner border
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(cardRadiusDp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.20f),
                                    Color.White.copy(alpha = 0.06f)
                                )
                            )
                        )
                        .drawBehind {
                            val strokeWidth = 1.dp.toPx()
                            val inset = strokeWidth / 2f + 0.5.dp.toPx()
                            val r = cardRadiusDp.toPx() - 0.5.dp.toPx()
                            val path = Path().apply {
                                addRoundRect(
                                    androidx.compose.ui.geometry.RoundRect(
                                        left = inset, top = inset,
                                        right = size.width - inset, bottom = size.height - inset,
                                        radiusX = r,
                                        radiusY = r
                                    )
                                )
                            }
                            drawPath(path, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = strokeWidth))
                        }
                ) {
                    // Gloss reflection strip
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(topStart = WayTheme.colors.radiusLg.dp, topEnd = WayTheme.colors.radiusLg.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0f))
                                )
                            )
                    )

                    Column(modifier = Modifier.padding(24.dp).padding(top = 8.dp)) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text("Email", color = loginDimColor) },
                            placeholder = { Text("you@example.com", color = loginDimmerColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = loginTextColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = loginDimColor,
                                unfocusedBorderColor = loginDimmerColor,
                                cursorColor = loginTextColor,
                                focusedLabelColor = loginDimColor,
                                unfocusedLabelColor = loginDimmerColor
                            )
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text("Password", color = loginDimColor) },
                            placeholder = { Text("••••••••", color = loginDimmerColor) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = loginTextColor),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = loginDimColor,
                                unfocusedBorderColor = loginDimmerColor,
                                cursorColor = loginTextColor,
                                focusedLabelColor = loginDimColor,
                                unfocusedLabelColor = loginDimmerColor
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(checkedColor = loginTextColor, uncheckedColor = loginDimColor, checkmarkColor = Color.Black))
                            Text("Remember me", fontSize = 13.sp, color = loginDimColor)
                        }
                        errorMessage?.let { msg ->
                            Text(text = msg, fontSize = 13.sp, color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp), textAlign = TextAlign.Center)
                        }
                        Spacer(Modifier.height(16.dp))
                        // Glossy aqua gel button
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(9999.dp))
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0xFF4DA6FF),
                                            Color(0xFF007AFF),
                                            Color(0xFF0055CC)
                                        )
                                    )
                                )
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    val inset = strokeWidth / 2f + 0.5.dp.toPx()
                                    val path = Path().apply {
                                        addRoundRect(
                                            androidx.compose.ui.geometry.RoundRect(
                                                left = inset, top = inset,
                                                right = size.width - inset, bottom = size.height - inset,
                                                radiusX = 9999.dp.toPx() - 0.5.dp.toPx(),
                                                radiusY = 9999.dp.toPx() - 0.5.dp.toPx()
                                            )
                                        )
                                    }
                                    drawPath(path, color = Color.White.copy(alpha = 0.35f), style = Stroke(width = strokeWidth))
                                }
                                .clickable(enabled = isValid && !isLoading) {
                                    if (isValid && !isLoading) {
                                        isLoading = true; errorMessage = null
                                        scope.launch {
                                            api.login(email, password).also { isLoading = false }
                                                .onSuccess {
                                                    if (rememberMe) TokenRepository.saveCredentials(email, password)
                                                    else TokenRepository.clearCredentials()
                                                    onLoginSuccess()
                                                }                                                .onFailure { 
                                                    println("LOGIN_ERROR_TYPE: ${it::class.simpleName}")
                                                    println("LOGIN_ERROR_MSG: ${it.message}")
                                                    println("LOGIN_ERROR_TOSTRING: $it")
                                                    errorMessage = it.message ?: "Login failed. Please try again." 
                                                }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Button gloss strip
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                                    .align(Alignment.TopCenter)
                                    .clip(RoundedCornerShape(9999.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                            )
                            Text(
                                if (isLoading) "Signing in…" else "Sign In",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = APP_VERSION,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.35f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return email.contains('@') && email.contains('.') && email.indexOf('@') < email.lastIndexOf('.')
}