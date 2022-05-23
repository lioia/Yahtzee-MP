package me.lioironzello.yahtzee.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// TODO(test sizes)

// Set of Material typography styles to start with
val SmallTypography = Typography(
    // Used for all texts
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    // Used for logo and special strings
    h1 = TextStyle(
        fontFamily = FontFamily.Cursive,
        fontWeight = FontWeight.Bold,
        fontSize = 96.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
)


val MediumTypography = Typography(
    // Used for all texts
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    // Used for logo and special strings
    h1 = TextStyle(
        fontFamily = FontFamily.Cursive,
        fontWeight = FontWeight.Bold,
        fontSize = 96.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 21.sp
    ),
)

val LargeTypography = Typography(
    // Used for all texts
    body1 = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp
    ),
    // Used for logo and special strings
    h1 = TextStyle(
        fontFamily = FontFamily.Cursive,
        fontWeight = FontWeight.Bold,
        fontSize = 96.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 42.sp
    ),
)

enum class TypeSize { Small, Medium, Large }

object Type {
    val TypeSizes = mapOf(
        TypeSize.Small to SmallTypography,
        TypeSize.Medium to MediumTypography,
        TypeSize.Large to LargeTypography
    )
}
