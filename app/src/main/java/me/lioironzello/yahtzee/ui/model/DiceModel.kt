package me.lioironzello.yahtzee.ui.model

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.google.ar.sceneform.rendering.RenderableInstance
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.colorOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class DiceModel(
    val is3D: Boolean,
    @IgnoredOnParcel
    val faces: List<Bitmap> = listOf(),
) : Parcelable {

    fun setModel(referenceNode: RenderableInstance?, color: Color) {
        referenceNode?.let {
            modelNode.setModel(it.renderable)
            modelNode.scaleModel(1.75f)
            modelNode.modelInstance?.getMaterial("background")?.filamentMaterialInstance?.setBaseColor(
                colorOf(color.toArgb())
            )
        }
    }

    // Random values used for 3D animation
    @IgnoredOnParcel
    var kx = 0

    @IgnoredOnParcel
    var ky = 0

    @IgnoredOnParcel
    private var _modelNode = mutableStateOf(ModelNode())
    var modelNode: ModelNode
        get() = _modelNode.value
        set(value) {
            _modelNode.value = value
        }

    // Random value used for 2D animation
    @IgnoredOnParcel
    var randomValue = 1

    @IgnoredOnParcel
    private var _number = mutableStateOf(1)
    var number: Int
        get() = _number.value
        set(value) {
            _number.value = value
        }

    @IgnoredOnParcel
    private var _locked = mutableStateOf(false)
    var locked: Boolean
        get() = _locked.value
        set(value) {
            _locked.value = value
        }

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
