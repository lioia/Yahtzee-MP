package me.lioironzello.yahtzee.ui.model

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.ui.theme.Background
import me.lioironzello.yahtzee.ui.theme.Dice

@Parcelize
class SettingsModel : Parcelable {
    @IgnoredOnParcel
    private var _language = mutableStateOf("")
    var language: String
        get() = _language.value
        set(value) {
            _language.value = value
        }

    @IgnoredOnParcel
    private var _darkTheme = mutableStateOf(false)
    var darkTheme: Boolean
        get() = _darkTheme.value
        set(value) {
            _darkTheme.value = value
        }

    @IgnoredOnParcel
    private var _background = mutableStateOf(Background.White)
    var background: Background
        get() = _background.value
        set(value) {
            _background.value = value
        }

    @IgnoredOnParcel
    private var _dice = mutableStateOf(Dice.White)
    var dice: Dice
        get() = _dice.value
        set(value) {
            _dice.value = value
        }

    @IgnoredOnParcel
    private var _diceVelocity = mutableStateOf(DiceVelocity.Medium)
    var diceVelocity: DiceVelocity
        get() = _diceVelocity.value
        set(value) {
            _diceVelocity.value = value
        }
}
