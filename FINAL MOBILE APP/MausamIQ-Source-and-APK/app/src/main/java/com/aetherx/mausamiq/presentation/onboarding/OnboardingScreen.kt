package com.aetherx.mausamiq.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandAmber
import com.aetherx.mausamiq.core.designsystem.BrandEmerald
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight
import com.aetherx.mausamiq.core.designsystem.CardBorderDark
import com.aetherx.mausamiq.core.designsystem.components.GlassCard
import com.aetherx.mausamiq.domain.model.LocationItem
import com.aetherx.mausamiq.domain.model.LocationType
import com.aetherx.mausamiq.domain.model.UserPersona

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingFinished: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

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
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Top Bar: Back & Step Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (state.currentStep > 1) {
                    IconButton(onClick = { viewModel.previousStep() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                Text(
                    text = "STEP ${state.currentStep} OF ${state.totalSteps}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = BrandPrimaryLight
                )

                Spacer(modifier = Modifier.size(48.dp))
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = BrandPrimaryLight,
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step Content Animated
            Box(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = state.currentStep,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    label = "onboarding_steps"
                ) { step ->
                    when (step) {
                        1 -> Step1Location(state, viewModel)
                        2 -> Step2Persona(state, viewModel)
                        3 -> Step3Interests(state, viewModel)
                        4 -> Step4Activities(state, viewModel)
                        5 -> Step5Schedule(state, viewModel)
                        6 -> Step6Locations(state, viewModel)
                        7 -> Step7Language(state, viewModel)
                    }
                }
            }

            // Bottom Navigation Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.currentStep < state.totalSteps) {
                    Button(
                        onClick = { viewModel.nextStep() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("CONTINUE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null)
                    }
                } else {
                    Button(
                        onClick = { viewModel.completeOnboarding(onOnboardingFinished) },
                        enabled = !state.isCompleting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                    ) {
                        if (state.isCompleting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("COMPLETE SETUP", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                            Spacer(modifier = Modifier.size(8.dp))
                            Icon(Icons.Rounded.Check, contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}

// STEP 1: Location
@Composable
private fun Step1Location(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Primary Location", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Choose your home city or base for hyper-local intelligence.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = state.searchQuery,
            onValueChange = { viewModel.searchLocation(it) },
            placeholder = { Text("Search Indian cities or towns...") },
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = BrandPrimaryLight) },
            trailingIcon = {
                if (state.isSearching) CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimaryLight,
                unfocusedBorderColor = Color(0xFF334155),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Use Current Location Button
        OutlinedButton(
            onClick = {
                viewModel.selectPrimaryLocation(
                    LocationItem(0, "New Delhi (Auto)", LocationType.HOME, 28.6139, 77.2090)
                )
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, BrandPrimaryLight.copy(alpha = 0.5f))
        ) {
            Icon(Icons.Rounded.MyLocation, contentDescription = null, tint = BrandPrimaryLight)
            Spacer(modifier = Modifier.size(8.dp))
            Text("Use Current Location (GPS / Network)", color = BrandPrimaryLight)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Location Preview
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x330284C7),
            borderColor = BrandPrimaryLight
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.LocationOn, contentDescription = null, tint = BrandPrimaryLight, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.size(12.dp))
                Column {
                    Text("Active Primary Location", style = MaterialTheme.typography.labelSmall, color = BrandPrimaryLight)
                    Text(state.selectedLocationName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Coordinates: ${String.format("%.4f", state.latitude)}, ${String.format("%.4f", state.longitude)}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF94A3B8))
                }
            }
        }

        // Search Results List
        if (state.searchResults.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Suggested Locations", style = MaterialTheme.typography.titleSmall, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            state.searchResults.forEach { loc ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    onClick = { viewModel.selectPrimaryLocation(loc) }
                ) {
                    Text(loc.name, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                }
            }
        }
    }
}

// STEP 2: Persona Selection
@Composable
private fun Step2Persona(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Select Persona", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Who are you? This customizes what weather factors MausamIQ prioritizes.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(16.dp))

        UserPersona.entries.forEach { persona ->
            val isSelected = state.selectedPersona == persona
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                backgroundColor = if (isSelected) Color(0x4D0284C7) else Color(0x331E293B),
                borderColor = if (isSelected) BrandPrimaryLight else CardBorderDark,
                onClick = { viewModel.selectPersona(persona) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(persona.iconEmoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.size(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = persona.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) BrandPrimaryLight else Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = persona.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = BrandPrimaryLight)
                    }
                }
            }
        }
    }
}

// STEP 3: Interests
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step3Interests(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val interests = listOf(
        "Travel", "Sports", "College", "Outdoor activities",
        "Commute", "Health & Air Quality", "Agriculture", "Events",
        "Photography", "Cycling", "Motorcycling", "Weekend Trips"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Your Interests", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Select the domains relevant to your daily routine (multi-select).", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            interests.forEach { interest ->
                val isSelected = state.selectedInterests.contains(interest)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleInterest(interest) },
                    label = { Text(interest, color = if (isSelected) Color.White else Color(0xFFCBD5E1)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        containerColor = Color(0xFF1E293B)
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) BrandPrimaryLight else Color(0xFF334155)
                    )
                )
            }
        }
    }
}

// STEP 4: Activities
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step4Activities(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val activities = listOf(
        "Walking", "Running", "Cycling", "College commute",
        "Intercity Travel", "Outdoor Construction", "Field Sports",
        "Farming & Spraying", "Evening Walks", "Public Transit"
    )
    var customText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Daily Activities", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Which outdoor activities do you regularly undertake?", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(20.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            activities.forEach { act ->
                val isSelected = state.selectedActivities.contains(act)
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.toggleActivity(act) },
                    label = { Text(act, color = if (isSelected) Color.White else Color(0xFFCBD5E1)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BrandPrimary,
                        containerColor = Color(0xFF1E293B)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Custom activity input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = customText,
                onValueChange = { customText = it },
                placeholder = { Text("Add custom activity...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimaryLight,
                    unfocusedBorderColor = Color(0xFF334155),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            IconButton(
                onClick = {
                    if (customText.isNotBlank()) {
                        viewModel.addCustomActivity(customText)
                        customText = ""
                    }
                },
                modifier = Modifier.background(BrandPrimary, CircleShape)
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    }
}

// STEP 5: Schedule
@Composable
private fun Step5Schedule(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Commute & Schedule", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Set your regular commute windows so MausamIQ calculates exact time-window risks.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(20.dp))

        // Departure Time
        GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = BrandPrimaryLight)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Morning Departure Time", style = MaterialTheme.typography.titleSmall, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.departureTime,
                    onValueChange = { viewModel.updateDepartureTime(it) },
                    placeholder = { Text("e.g. 08:30 AM") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimaryLight,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Return Time
        GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = BrandAmber)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Evening Return Time", style = MaterialTheme.typography.titleSmall, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.returnTime,
                    onValueChange = { viewModel.updateReturnTime(it) },
                    placeholder = { Text("e.g. 05:00 PM") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimaryLight,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // Workout Time
        GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Schedule, contentDescription = null, tint = BrandEmerald)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Workout / Outdoor Activity Time", style = MaterialTheme.typography.titleSmall, color = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.workoutTime,
                    onValueChange = { viewModel.updateWorkoutTime(it) },
                    placeholder = { Text("e.g. 06:30 AM") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimaryLight,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }
    }
}

// STEP 6: Important Locations
@Composable
private fun Step6Locations(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    var locName by remember { mutableStateOf("") }
    var locType by remember { mutableStateOf(LocationType.COLLEGE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Important Locations", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Add key places like College, Work, or Farm for commute intelligence.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(16.dp))

        // Existing Locations
        state.savedLocations.forEach { loc ->
            GlassCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(loc.type.iconEmoji, fontSize = 20.sp)
                        Spacer(modifier = Modifier.size(10.dp))
                        Column {
                            Text(loc.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                            Text(loc.type.label, style = MaterialTheme.typography.labelSmall, color = BrandPrimaryLight)
                        }
                    }
                    if (!loc.isPrimary) {
                        IconButton(onClick = { viewModel.removeSavedLocation(loc) }) {
                            Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add Location Input Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0x331E293B),
            borderColor = CardBorderDark
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Add New Location", style = MaterialTheme.typography.titleSmall, color = Color.White)
                OutlinedTextField(
                    value = locName,
                    onValueChange = { locName = it },
                    placeholder = { Text("e.g. Science Block, Tech Campus") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimaryLight,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LocationType.entries.forEach { type ->
                        FilterChip(
                            selected = locType == type,
                            onClick = { locType = type },
                            label = { Text("${type.iconEmoji} ${type.label}") }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (locName.isNotBlank()) {
                            viewModel.addSavedLocation(locName, locType, 28.5450, 77.1926)
                            locName = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Save Location")
                }
            }
        }
    }
}

// STEP 7: Language
@Composable
private fun Step7Language(state: OnboardingUiState, viewModel: OnboardingViewModel) {
    val languages = listOf(
        Triple("en", "English", "Weather Intelligence"),
        Triple("ta", "தமிழ் (Tamil)", "வானிலை நுண்ணறிவு"),
        Triple("hi", "हिन्दी (Hindi)", "मौसम बुद्धिमत्ता")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text("Language Preference", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Choose your preferred language for the application UI.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.height(20.dp))

        languages.forEach { (code, name, subtitle) ->
            val isSelected = state.selectedLanguage == code
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                backgroundColor = if (isSelected) Color(0x4D0284C7) else Color(0x331E293B),
                borderColor = if (isSelected) BrandPrimaryLight else CardBorderDark,
                onClick = { viewModel.selectLanguage(code) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) BrandPrimaryLight else Color.White
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                    if (isSelected) {
                        Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = BrandPrimaryLight)
                    }
                }
            }
        }
    }
}
