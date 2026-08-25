package com.aetherx.mausamiq.presentation.settings

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.BrandRose
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandPrimary.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = BrandPrimaryLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "Preferences, system parameters & SIH mode",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            // 1. SIH Demo Mode Switch Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = if (state.isDemoMode) Color(0x660284C7) else Color(0x660F172A),
                borderColor = if (state.isDemoMode) BrandPrimaryLight else CardBorderDark
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(BrandAmber.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Bolt,
                                contentDescription = null,
                                tint = BrandAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(12.dp))
                        Column {
                            Text(
                                text = "SIH 2026 Demo Mode",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "Inject curated student, farmer & traveller jury scenarios without depending on live network.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFCBD5E1)
                            )
                        }
                    }

                    Switch(
                        checked = state.isDemoMode,
                        onCheckedChange = { viewModel.toggleDemoMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = BrandPrimary,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFF1E293B)
                        )
                    )
                }
            }

            // 2. Language Selection
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Language, contentDescription = null, tint = BrandPrimaryLight)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Application Language", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("en" to "English", "ta" to "தமிழ்", "hi" to "हिन्दी").forEach { (code, name) ->
                            val isSelected = state.language == code
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) BrandPrimary else Color(0xFF1E293B),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setLanguage(code) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 3. Theme Mode
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Palette, contentDescription = null, tint = BrandPrimaryLight)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Visual Appearance", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("DARK" to "Dark (Atmospheric)", "LIGHT" to "Light", "SYSTEM" to "System").forEach { (themeKey, name) ->
                            val isSelected = state.themeMode == themeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) BrandPrimary else Color(0xFF1E293B),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.setThemeMode(themeKey) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 4. About SIH26076 Team AETHERX
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.toggleAboutDialog(true) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = BrandPrimaryLight)
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text("About MausamIQ & Team AETHERX", style = MaterialTheme.typography.titleSmall, color = Color.White)
                            Text("Smart India Hackathon 2026 • SIH26076", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            // 5. Logout Button
            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33EF4444))
            ) {
                Icon(Icons.Rounded.Logout, contentDescription = null, tint = BrandRose)
                Spacer(modifier = Modifier.size(8.dp))
                Text("LOG OUT OF SESSION", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = BrandRose)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // About Dialog
        if (state.showAboutDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.toggleAboutDialog(false) },
                title = { Text("MAUSAMIQ • SIH 2026", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Team: AETHERX", style = MaterialTheme.typography.titleSmall, color = BrandPrimaryLight)
                        Text("Problem Statement: SIH26076", style = MaterialTheme.typography.bodySmall, color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "\"Not Just Weather. Weather That Matters to You.\"",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandAmber
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "MausamIQ is a native Android AI-powered Personalized Weather Intelligence and Decision-Support System. Built using Jetpack Compose, Clean Architecture + MVVM, Room, Open-Meteo, and Explainable AI Engine.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.toggleAboutDialog(false) }) {
                        Text("Close", color = BrandPrimaryLight)
                    }
                },
                containerColor = Color(0xFF0F172A)
            )
        }
    }
}
