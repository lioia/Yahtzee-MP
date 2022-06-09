package me.lioironzello.yahtzee.model

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class SettingsModel : Parcelable {
    @IgnoredOnParcel
    private var _darkTheme = mutableStateOf(false)
    var darkTheme: Boolean
        get() = _darkTheme.value
        set(value) {
            _darkTheme.value = value
        }

    @IgnoredOnParcel
    private var _diceColor = mutableStateOf(DiceColor.White)
    var diceColor: DiceColor
        get() = _diceColor.value
        set(value) {
            _diceColor.value = value
        }

    @IgnoredOnParcel
    private var _diceVelocity = mutableStateOf(DiceVelocity.Medium)
    var diceVelocity: DiceVelocity
        get() = _diceVelocity.value
        set(value) {
            _diceVelocity.value = value
        }

    @IgnoredOnParcel
    private var _soundEnabled = mutableStateOf(true)
    var soundEnabled: Boolean
        get() = _soundEnabled.value
        set(value) {
            _soundEnabled.value = value
        }

    @IgnoredOnParcel
    var glVersion: Int = 1
}
