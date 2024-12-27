package com.example.courseworkmobile.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

import com.example.courseworkmobile.R


val customFont = FontFamily(
    Font(R.font.jost_regular),
    Font(R.font.jost_medium, FontWeight.Medium),
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = customFont,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = customFont,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = customFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = customFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = customFont,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = customFont,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = customFont,
        fontSize = 12.sp,
        color = OnlyNewsSecondaryText
    )
)