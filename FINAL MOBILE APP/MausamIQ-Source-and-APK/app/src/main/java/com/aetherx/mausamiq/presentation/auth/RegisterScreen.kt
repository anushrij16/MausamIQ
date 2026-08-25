package com.aetherx.mausamiq.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandEmerald
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    val passwordStrength = calculatePasswordStrength(state.password)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0F172A),
                        Color(0xFF070B12)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set up your tailored weather decision profile",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(24.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x660F172A),
                borderColor = CardBorderDark
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Error Banner
                    if (state.errorMessage != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BrandRose.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ErrorOutline,
                                contentDescription = null,
                                tint = BrandRose,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = state.errorMessage ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandRose
                            )
                        }
                    }

                    // Full Name Field
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Person, contentDescription = null, tint = BrandPrimaryLight)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrandPrimaryLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    // Email Field
                    OutlinedTextField(
                        value = state.email,
                        onValueChange = { viewModel.onEmailChange(it) },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Email, contentDescription = null, tint = BrandPrimaryLight)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrandPrimaryLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    // Phone Field (Optional)
                    OutlinedTextField(
                        value = state.phone,
                        onValueChange = { viewModel.onPhoneChange(it) },
                        label = { Text("Phone Number (Optional)") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Phone, contentDescription = null, tint = BrandPrimaryLight)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrandPrimaryLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    // Password Field
                    OutlinedTextField(
                        value = state.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("Password (min. 8 chars)") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = BrandPrimaryLight)
                        },
                        trailingIcon = {
                            IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                                Icon(
                                    imageVector = if (state.isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color(0xFF94A3B8)
                                )
                            }
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrandPrimaryLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    // Password Strength Indicator
                    if (state.password.isNotEmpty()) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Password Strength: ${passwordStrength.label}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = passwordStrength.color
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { passwordStrength.score },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = passwordStrength.color,
                                trackColor = Color(0xFF334155)
                            )
                        }
                    }

                    // Confirm Password Field
                    OutlinedTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.onConfirmPasswordChange(it) },
                        label = { Text("Confirm Password") },
                        leadingIcon = {
                            Icon(Icons.Rounded.Lock, contentDescription = null, tint = BrandPrimaryLight)
                        },
                        visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimaryLight,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = BrandPrimaryLight,
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        )
                    )

                    // Terms Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = state.termsAccepted,
                            onCheckedChange = { viewModel.toggleTermsAccepted() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = BrandPrimary,
                                uncheckedColor = Color(0xFF64748B)
                            )
                        )
                        Text(
                            text = "I agree to Terms of Service & Privacy Policy",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCBD5E1)
                        )
                    }

                    // Submit Button
                    Button(
                        onClick = { viewModel.register(onRegisterSuccess) },
                        enabled = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "CREATE ACCOUNT",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation to Login
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF94A3B8)
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimaryLight,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

private data class PasswordStrength(
    val score: Float,
    val label: String,
    val color: Color
)

private fun calculatePasswordStrength(password: String): PasswordStrength {
    var points = 0
    if (password.length >= 8) points++
    if (password.any { it.isUpperCase() }) points++
    if (password.any { it.isDigit() }) points++
    if (password.any { !it.isLetterOrDigit() }) points++

    return when (points) {
        0, 1 -> PasswordStrength(0.25f, "Weak", BrandRose)
        2, 3 -> PasswordStrength(0.65f, "Moderate", BrandAmber)
        else -> PasswordStrength(1.0f, "Strong", BrandEmerald)
    }
}
