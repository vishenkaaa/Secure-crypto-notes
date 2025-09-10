package com.example.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.presentation.extension.toIcon
import com.example.presentation.extension.toTitle

@Composable
fun AppNavigationBar(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    Box {
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavigationShadow()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 6.dp, bottom = 16.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(30.dp),
                    clip = false,
                    ambientColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopLevelDestinations.entries.forEach { screen ->
                val isSelected = currentDestination?.route == screen.route::class.qualifiedName

                CustomNavigationBarItem(
                    selected = isSelected,
                    tab = screen,
                    modifier = Modifier.weight(1f)
                ) {
                    navController.navigate(screen.route)
                }
            }
        }
    }
}

@Composable
fun NavigationShadow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background.copy(alpha = 0.0f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                        MaterialTheme.colorScheme.background,
                    )
                )
            )
    )
}

@Composable
fun CustomNavigationBarItem(
    selected: Boolean,
    tab: TopLevelDestinations,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
){
    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    ) {
        Icon(
            painter = tab.toIcon(),
            modifier = Modifier.size(24.dp),
            contentDescription = tab.contentDescription,
            tint = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = tab.toTitle(),
            color = if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
            style = MaterialTheme.typography.bodySmall
        )
    }
}