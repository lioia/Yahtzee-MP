package me.lioironzello.yahtzee.model

data class GameState(val dices: List<Int>, val currentRoll: Int, val players: List<Player>)
