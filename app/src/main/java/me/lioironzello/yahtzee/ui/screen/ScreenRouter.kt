package me.lioironzello.yahtzee.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ScreenRouter {
    var currentScreen by mutableStateOf(Screens.Home)

    fun navigateTo(destination: Screens) {
        currentScreen = destination
    }
}

enum class Screens { Home, Settings, PreviousGames, Tutorial, Play }
