package me.lioironzello.yahtzee.model

data class LastGameState(val dices: List<Int>, val currentRoll: Int, val players: List<Player>)
