package com.example.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.example.presentation.extension.toIcon
import com.example.presentation.extension.toTitle

@Composable
fun AppNavigationBar(
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavigationShadow()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(32.dp),
                    clip = false
                )
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.primary.copy(0.6f))
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopLevelDestinations.entries.forEach { screen ->
                val isSelected = currentDestination?.route == screen.route::class.qualifiedName

                ModernNavigationBarItem(
                    selected = isSelected,
                    tab = screen,
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(screen.route) }
                )
            }
        }
    }
}

@Composable
fun NavigationShadow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
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
fun ModernNavigationBarItem(
    selected: Boolean,
    tab: TopLevelDestinations,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onPrimary.copy(0.5f),
        animationSpec = tween(300),
        label = "color"
    )

    val animatedBackgroundColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primary
        else Color.Transparent,
        animationSpec = tween(300),
        label = "backgroundColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(animatedBackgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = tab.toIcon(),
                    contentDescription = tab.contentDescription,
                    tint = animatedColor,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tab.toTitle(),
                    color = animatedColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            Icon(
                painter = tab.toIcon(),
                contentDescription = tab.contentDescription,
                tint = animatedColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}