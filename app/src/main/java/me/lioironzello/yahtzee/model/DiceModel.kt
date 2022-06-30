package me.lioironzello.yahtzee.model

import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.R

enum class DiceVelocity(val text: Int) { Slow(R.string.slow), Medium(R.string.medium), Fast(R.string.fast) }
enum class DiceColor(val color: Color) {
    White(Color.White),
    Blue(Color.Blue),
    Green(Color.Green),
    Red(Color.Red)
}

@Parcelize
class DiceModel(val is3D: Boolean, private val value: Int) : Parcelable {
    // Selected values for 3D animation
    @IgnoredOnParcel
    var rotation = Values3D[value]

    // Random value used for 2D animation
    @IgnoredOnParcel
    var randomValue = value

    // Current value of the dice (0..5)
    @IgnoredOnParcel
    private var _number = mutableStateOf(value)
    var number: Int
        get() = _number.value
        set(value) {
            _number.value = value
        }

    // Dice can't be rolled
    @IgnoredOnParcel
    private var _locked = mutableStateOf(false)
    var locked: Boolean
        get() = _locked.value
        set(value) {
            _locked.value = value
        }

    // Utility object for the 3D faces
    companion object {
        val Values3D = listOf(
            Pair(2, 3), // Face 1
            Pair(2, 2), // Face 2
            Pair(1, 2), // Face 3
            Pair(3, 2), // Face 4
            Pair(2, 4), // Face 5
            Pair(3, 1), // Face 6
        )
    }
}
