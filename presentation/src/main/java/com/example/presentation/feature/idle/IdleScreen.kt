package com.example.presentation.feature.idle

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.presentation.R
import com.example.presentation.common.ui.values.SecureCryptoNotesTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun IdleScreen() {
    SplashScreen()
}

@Composable
fun SplashScreen() {
    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.3f) }
    val textAlpha = remember { Animatable(0f) }
    val textTranslationY = remember { Animatable(50f) }

    LaunchedEffect(key1 = true) {
        launch {
            logoScale.animateTo(
                1f,
                animationSpec = tween(1200, easing = FastOutSlowInEasing)
            )
        }
        launch {
            logoAlpha.animateTo(
                1f,
                animationSpec = tween(1000, delayMillis = 300)
            )
        }

        delay(800)
        launch {
            textTranslationY.animateTo(
                0f,
                animationSpec = tween(1000, easing = FastOutSlowInEasing)
            )
        }
        launch {
            textAlpha.animateTo(
                1f,
                animationSpec = tween(800, delayMillis = 200)
            )
        }

        delay(2000)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Image(
            painter = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp, bottom = 120.dp, start = 32.dp, end = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "App logo",
                modifier = Modifier
                    .size(200.dp)
                    .alpha(logoAlpha.value)
                    .scale(logoScale.value)
            )

            Text(
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .fillMaxWidth()
                    .offset { IntOffset(0, textTranslationY.value.dp.roundToPx()) },
                text = stringResource(R.string.secure_crypto_notes),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Preview
@Composable
fun SplashScreenPreview(){
    SecureCryptoNotesTheme {
        SplashScreen()
    }
}
