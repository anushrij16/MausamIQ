package com.aetherx.mausamiq.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aetherx.mausamiq.core.designsystem.BrandPrimary
import com.aetherx.mausamiq.core.designsystem.BrandPrimaryLight

@Composable
fun MausamBottomNavBar(
    currentDestination: NavigationDestination,
    onNavigateTo: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .navigationBarsPadding()
            .height(68.dp),
        containerColor = Color(0xFF0B111E).copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        NavigationDestination.bottomNavDestinations.forEach { dest ->
            val isSelected = currentDestination == dest
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateTo(dest) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.title,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = dest.title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BrandPrimaryLight,
                    selectedTextColor = BrandPrimaryLight,
                    indicatorColor = Color(0x330284C7),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                )
            )
        }
    }
}

@Composable
fun MausamNavRail(
    currentDestination: NavigationDestination,
    onNavigateTo: (NavigationDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color(0xFF0B111E).copy(alpha = 0.95f)
    ) {
        NavigationDestination.entries.forEach { dest ->
            val isSelected = currentDestination == dest
            NavigationRailItem(
                selected = isSelected,
                onClick = { onNavigateTo(dest) },
                icon = {
                    Icon(
                        imageVector = dest.icon,
                        contentDescription = dest.title
                    )
                },
                label = {
                    Text(dest.title, style = MaterialTheme.typography.labelSmall)
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = BrandPrimaryLight,
                    selectedTextColor = BrandPrimaryLight,
                    indicatorColor = Color(0x330284C7),
                    unselectedIconColor = Color(0xFF64748B),
                    unselectedTextColor = Color(0xFF64748B)
                )
            )
        }
    }
}
