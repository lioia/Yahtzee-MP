package me.lioironzello.yahtzee.model

import me.lioironzello.yahtzee.ui.screen.ScoreType

class Player {
    var scores = mutableMapOf<ScoreType, Int>()
    var bonusReached = false
    var lastSixScore = 0
    var doubleYahtzee = false

    fun calculateBonus() {
        if (bonusReached) return
        val lastSix = scores.values.toList().takeLast(6)
        bonusReached = lastSix.sum() >= 63
        lastSixScore = lastSix.sum()
    }
}
