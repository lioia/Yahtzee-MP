package me.lioironzello.yahtzee.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Object used to save the current screen
// Enum is used to have a symbolic name of the screen instead of using an integer
object ScreenRouter {
    var currentScreen by mutableStateOf(Screens.Home)

    fun navigateTo(destination: Screens) {
        currentScreen = destination
    }
}

enum class Screens { Home, Settings, PreviousGames, Tutorial, Play }
