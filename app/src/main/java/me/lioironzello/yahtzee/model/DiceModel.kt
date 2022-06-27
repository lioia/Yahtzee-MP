package me.lioironzello.yahtzee.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.ar.sceneform.rendering.RenderableInstance
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.colorOf
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
class DiceModel(
    val is3D: Boolean,
    @IgnoredOnParcel
    val faces: List<Bitmap> = listOf(),
) : Parcelable {
    // Update the background color and scale the model
    fun init(referenceNode: RenderableInstance?, color: Color, number: Int) {
        this.number = number
        randomValue = number
        kx = Values3D[number].first
        ky = Values3D[number].second
        referenceNode?.let {
            modelNode.setModel(it.renderable)
            modelNode.scaleModel(1.75f)
            modelNode.modelInstance?.getMaterial("background")?.filamentMaterialInstance?.setBaseColor(
                colorOf(color.toArgb())
            )
            modelNode.rotation = Rotation(kx * 90f, ky * 90f, 90f)
        }
    }

    // Random values used for 3D animation
    @IgnoredOnParcel
    var kx = 0

    @IgnoredOnParcel
    var ky = 0

    // 3D model
    @IgnoredOnParcel
    var modelNode by mutableStateOf(ModelNode())
//    var modelNode: ModelNode
//        get() = _modelNode.value
//        set(value) {
//            _modelNode.value = value
//        }

    // Random value used for 2D animation
    @IgnoredOnParcel
    var randomValue = 1

    // Current value of the dice (0..5)
    @IgnoredOnParcel
    private var _number = mutableStateOf(1)
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

    // Utility object for the 3d faces
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
