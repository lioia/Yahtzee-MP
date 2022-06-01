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
//    @IgnoredOnParcel
//    val referenceNode: RenderableInstance? = null,
    @IgnoredOnParcel
    val faces: List<Bitmap> = listOf(),
) : Parcelable {
//    init {
//        if (referenceNode != null) {
//            modelNode.setModel(referenceNode.renderable)
//            modelNode.scaleModel(1.75f)
//        }
//    }

    fun setModel(referenceNode: RenderableInstance?, color: Color) {
        referenceNode?.let {
            modelNode.setModel(it.renderable)
            modelNode.scaleModel(1.75f)
            modelNode.modelInstance?.getMaterial("background")?.filamentMaterialInstance?.setBaseColor(
                colorOf(color.toArgb())
            )
        }
    }

//    @IgnoredOnParcel
//    var is3D = false

    // Random values used for 3D animation
    @IgnoredOnParcel
    var kx = 0

    @IgnoredOnParcel
    var ky = 0
//    @IgnoredOnParcel
//    private var _kx = mutableStateOf(0)
//    var kx: Int
//        get() = _kx.value
//        set(value) {
//            _kx.value = value
//        }
//
//    @IgnoredOnParcel
//    private var _ky = mutableStateOf(0)
//    var ky: Int
//        get() = _ky.value
//        set(value) {
//            _ky.value = value
//        }

    @IgnoredOnParcel
    private var _modelNode = mutableStateOf(ModelNode())
    var modelNode: ModelNode
        get() = _modelNode.value
        set(value) {
            _modelNode.value = value
        }

//    @IgnoredOnParcel
//    var faces = listOf<Bitmap>()

    // Random value used for 2D animation
    @IgnoredOnParcel
    var randomValue = 1
//    private var _randomValue = mutableStateOf(1)
//    var randomValue: Int
//        get() = _randomValue.value
//        set(value) {
//            _randomValue.value = value
//        }

    @IgnoredOnParcel
    private var _number = mutableStateOf(1)
    var number: Int
        get() = _number.value
        set(value) {
            _number.value = value
        }
}
