package me.lioironzello.yahtzee.ui.theme

import androidx.compose.ui.graphics.Color

val Purple200 = Color(0xFFBB86FC)
val Purple500 = Color(0xFF6200EE)
val Purple700 = Color(0xFF3700B3)
val Teal200 = Color(0xFF03DAC5)

// White and black are solid colors
// Blue, green and wood are background images
enum class Background { White, Black, Blue, Green, Wood }

// Background Color
enum class Dice(val color: Color) {
    White(Color.White), Blue(Color.Blue), Green(Color.Green), Red(
        Color.Red
    )
}
