package com.example.presentation.common.ui.values

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.presentation.R

val gilroyFontFamily = FontFamily(
    Font(R.font.gilroy_bold, FontWeight.Bold),
    Font(R.font.gilroy_medium, FontWeight.Medium),
    Font(R.font.gilroy_regular, FontWeight.Normal)
)

val display1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val title1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val headline1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val headline2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val body1: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val body2: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val body3: TextStyle
    @Composable
    get() = TextStyle(
        fontFamily = gilroyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        color = MaterialTheme.colorScheme.onBackground
    )

val Typography: Typography
    @Composable
    get() = Typography(
        displayLarge = display1,
        headlineLarge = headline1,
        headlineMedium = headline2,
        titleLarge = title1,
        bodyLarge = body1,
        bodyMedium = body2,
        bodySmall = body3
    )