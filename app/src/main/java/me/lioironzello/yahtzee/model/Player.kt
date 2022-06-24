package me.lioironzello.yahtzee.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.ui.screen.ScoreType

@Parcelize
data class Player(
    var scores: MutableMap<ScoreType, Int> = mutableMapOf(),
    var bonusReached: Boolean = false,
    var lastSixScore: Int = 0,
    var doubleYahtzee: Boolean = false
) : Parcelable

fun Player.calculateBonus() {
    if (bonusReached) return
    val lastSix = scores.values.toList().takeLast(6)
    bonusReached = lastSix.sum() >= 63
    lastSixScore = lastSix.sum()
}
