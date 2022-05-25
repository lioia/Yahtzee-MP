package me.lioironzello.yahtzee.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.ui.theme.Background
import me.lioironzello.yahtzee.ui.theme.Dice

@Parcelize
class SettingsModel(
    var language: String,
    var darkTheme: Boolean,
    val background: Background,
    val dice: Dice,
    var diceVelocity: DiceVelocity
) : Parcelable
