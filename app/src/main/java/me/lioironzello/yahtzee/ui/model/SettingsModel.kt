package me.lioironzello.yahtzee.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.ui.theme.Background
import me.lioironzello.yahtzee.ui.theme.Dice
import me.lioironzello.yahtzee.ui.theme.TypeSize

@Parcelize
data class SettingsModel(
    val darkTheme: Boolean,
    val textSize: TypeSize,
    val background: Background,
    val dice: Dice,
    val diceVelocity: DiceVelocity
) : Parcelable
