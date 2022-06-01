package me.lioironzello.yahtzee.ui.screen

import android.graphics.Bitmap
import android.graphics.Canvas
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateInt
import androidx.compose.animation.core.animateOffset
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.google.ar.sceneform.rendering.RenderableInstance
import io.github.sceneview.SceneView
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.colorOf
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.ui.model.DiceModel
import me.lioironzello.yahtzee.ui.model.SettingsModel
import kotlin.random.Random

@Composable
fun PlayLayout(settingsModel: SettingsModel) {
    var referenceModel by remember { mutableStateOf<RenderableInstance?>(null) }
    var faces = listOf<Bitmap>()

    val context = LocalContext.current

    // Load 3D model
    if (settingsModel.diceVelocity == DiceVelocity.Slow) {
        val lifecycleOwner = LocalLifecycleOwner.current

        lifecycleOwner.lifecycleScope.launchWhenCreated {
            if (referenceModel == null) {
                referenceModel = ModelNode().loadModel(
                    context = context,
                    lifecycle = lifecycleOwner.lifecycle,
                    glbFileLocation = "cube.glb",
                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f)
                )
                referenceModel?.let {
                    it.getMaterial("background")?.filamentMaterialInstance?.setBaseColor(
                        colorOf(settingsModel.dice.color.toArgb())
                    )
                }
            }

        }

    } else {
        val density = context.resources.displayMetrics.density
        val size = 64 * density
        val radius = 64 / 14 * density
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = 4f
        }
        // Create 2D faces
        faces = List<Bitmap>(6) {
            Bitmap.createBitmap(
                size.toInt(),
                size.toInt(),
                Bitmap.Config.ARGB_8888
            )
        }
        faces.forEachIndexed { index, bitmap ->
            val canvas = Canvas(bitmap)
            canvas.drawColor(settingsModel.dice.color.toArgb())
            canvas.drawLine(0f, 0f, 0f, size, paint)
            canvas.drawLine(0f, 0f, size, 0f, paint)
            canvas.drawLine(size, 0f, size, size, paint)
            canvas.drawLine(0f, size, size, size, paint)
            when (index) {
                // Face 1
                0 -> canvas.drawCircle(size / 2, size / 2, radius, paint)
                // Face 2
                1 -> {
                    canvas.drawCircle(size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 3, radius, paint)
                }
                // Face 3
                2 -> {
                    canvas.drawCircle(size / 2, size / 2, radius, paint)
                    canvas.drawCircle(size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 3, radius, paint)
                }
                // Face 4
                3 -> {
                    canvas.drawCircle(size / 3, size / 3, radius, paint)
                    canvas.drawCircle(size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, 2 * size / 3, radius, paint)
                }
                // Face 5
                4 -> {
                    canvas.drawCircle(size / 2, size / 2, radius, paint)
                    canvas.drawCircle(size / 3, size / 3, radius, paint)
                    canvas.drawCircle(size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, 2 * size / 3, radius, paint)
                }
                // Face 6
                5 -> {
                    canvas.drawCircle(size / 3, size / 3, radius, paint)
                    canvas.drawCircle(size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 3, radius, paint)
                    canvas.drawCircle(2 * size / 3, 2 * size / 3, radius, paint)
                    canvas.drawCircle(size / 3, size / 2, radius, paint)
                    canvas.drawCircle(2 * size / 3, size / 2, radius, paint)
                }
            }
        }
    }

    if (settingsModel.diceVelocity == DiceVelocity.Slow) {
        if (referenceModel != null)
            Play(settingsModel, referenceModel, faces)
        else {
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.loading))
            }
        }
    } else {
        Play(settingsModel, null, faces)
    }
}

@Composable
fun Play(settingsModel: SettingsModel, referenceModel: RenderableInstance?, faces: List<Bitmap>) {
    val is3D = settingsModel.diceVelocity == DiceVelocity.Slow
    val dices = remember {
        mutableStateListOf(
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.dice.color) },
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.dice.color) },
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.dice.color) },
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.dice.color) },
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.dice.color) }
        )
    }

    var currentRound by remember { mutableStateOf(0) }
    var currentRoll by remember { mutableStateOf(0) }
    var currentPlayer by remember { mutableStateOf(0) }
    val players = remember { mutableStateListOf<Player>() }
    var animate by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = animate, label = "Cube")

    Column(
        Modifier.fillMaxSize()
    ) {
        ScoreBoard(dices, players, currentPlayer)
        // Dices
        Row(
            Modifier
                .fillMaxWidth()
                .height(80.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dices.forEach {
                when (settingsModel.diceVelocity) {
                    DiceVelocity.Slow -> {
                        val rotation by transition.animateOffset(
                            label = "Cube",
                            transitionSpec = { tween(durationMillis = 1000) }) { state ->
                            if (state) Offset(
                                it.kx * 90f,
                                it.ky * 90f
                            ) else Offset(it.modelNode.rotation.x, it.modelNode.rotation.y)
                        }
                        it.modelNode.rotation = Rotation(rotation.x, rotation.y, 90f)

                        AndroidView(modifier = Modifier.size(80.dp), factory = { context ->
                            SceneView(context).apply {
                                mainLight = null
                                // TODO(background image)
                                backgroundColor = colorOf(255f, 255f, 255f)
                                addChild(it.modelNode)
                                gestureDetector.moveGestureDetector = null
                                gestureDetector.rotateGestureDetector = null
                                gestureDetector.scaleGestureDetector = null
                                gestureDetector.cameraManipulator = null
                            }
                        })
                    }
                    else -> {
                        val value = if (settingsModel.diceVelocity == DiceVelocity.Medium) {
                            transition.animateInt(
                                label = "Cube",
                                transitionSpec = { tween(durationMillis = 500) }) { state ->
                                if (state) it.randomValue else it.number
                            }.value
                        } else {
                            it.randomValue
                        }

                        Image(
                            it.faces[value % 6].asImageBitmap(),
                            modifier = Modifier.padding(2.dp),
                            contentDescription = "Face"
                        )
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                currentRoll++
                dices.forEach {
                    val random = Random.nextInt(6, 36)
                    it.number = random % 6
                    if (it.is3D) {
                        it.kx = DiceModel.Values3D[it.number].first + Random.nextInt(1, 5) * 4
                        it.ky = DiceModel.Values3D[it.number].second + Random.nextInt(1, 5) * 4
                        println("${it.number}, ${it.kx}, ${it.ky}")
                    } else {
                        it.randomValue = random
                    }
                    animate = true
                }
            }, enabled = currentRoll < 3) {
                Text("Roll")
            }
            Button(onClick = {
                currentRoll = 0
                if (currentPlayer == players.size) {
                    currentPlayer = 0
                    currentRound++
                } else {
                    currentPlayer++
                }
            }, enabled = currentRoll > 0) {
                Text("Next Round")
            }
        }
        // TODO(dialog game finished)
        BackHandler { ScreenRouter.navigateTo(Screens.Home) }
    }
}

@Composable
fun ScoreBoard(dices: List<DiceModel>, players: List<Player>, currentPlayer: Int) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Column {
        }
        Column {}
    }
}

@Composable
fun Score(dices: List<DiceModel>, players: List<Player>, currentPlayer: Int, type: ScoreType) {
    Row {
        Text(type.name) // TODO(translate)
        players.forEachIndexed { index, player ->
            var score = " "
            val savedScore = player.scores[type]
            if (savedScore == null) {
                if (currentPlayer == index) {
                    val count = calculateScore(dices.map { dice -> dice.number }, type)
                    score = count.toString()
                }
            } else score = savedScore.toString()
            Text(score)
        }
    }
}

enum class ScoreType { One, Two, Three, Four, Five, Six, Tris, Poker, Full, SmallStraight, LargeStraight, Chance, Yahtzee }

class Player {
    val scores = mapOf<ScoreType, Int>()
    val bonusReached = false
}

fun calculateScore(dices: List<Int>, type: ScoreType): Int {
    when (type) {
        ScoreType.One -> return dices.filter { dice -> dice == 1 }.size
        ScoreType.Two -> return dices.filter { dice -> dice == 2 }.size * 2
        ScoreType.Three -> return dices.filter { dice -> dice == 3 }.size * 3
        ScoreType.Four -> return dices.filter { dice -> dice == 4 }.size * 4
        ScoreType.Five -> return dices.filter { dice -> dice == 5 }.size * 5
        ScoreType.Six -> return dices.filter { dice -> dice == 6 }.size * 6
        ScoreType.Tris -> return if (dices.distinct().size <= 3) dices.sum() else 0
        ScoreType.Poker -> return if (dices.distinct().size <= 2) dices.sum() else 0
        ScoreType.Full -> {
            val first = dices.first()
            val subList1 = dices.filter { dice -> dice == first }
            val subList2 = dices.filter { dice -> dice != first }
            if (subList2.distinct().size != 1) return 0
            if (subList1.size >= 2 && subList2.size >= 2) return 25
            return 0
        }
        ScoreType.SmallStraight -> {
            if (dices.distinct().size < 4) return 0
            if (dices.contains(3) && dices.contains(4)) {
                if (dices.contains(2)) {
                    if (dices.contains(1) || dices.contains(5)) return 30
                } else if (dices.contains(5) && dices.contains(6)) {
                    return 30
                }
            }
            return 0
        }
        ScoreType.LargeStraight -> return if (dices.distinct().size == 5 && (dices.sum() == 15 || dices.sum() == 20)) 40 else 0
        ScoreType.Chance -> return dices.sum()
        ScoreType.Yahtzee -> return if (dices.distinct().size == 1) 50 else 0
    }
}
