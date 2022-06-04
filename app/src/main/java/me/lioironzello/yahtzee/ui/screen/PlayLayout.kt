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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun PlayLayout(settingsModel: SettingsModel, numberOfPlayers: Int) {
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
            Play(settingsModel, numberOfPlayers, referenceModel, faces)
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
        Play(settingsModel, numberOfPlayers, null, faces)
    }
}

@Composable
fun Play(
    settingsModel: SettingsModel,
    numberOfPlayers: Int,
    referenceModel: RenderableInstance?,
    faces: List<Bitmap>
) {
    val context = LocalContext.current

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
    val players = if (numberOfPlayers == 1) {
        remember { mutableStateListOf(Player()) }
    } else {
        remember { mutableStateListOf(Player(), Player()) }
    }
    var animate by remember { mutableStateOf(false) }
    val transition = updateTransition(targetState = animate, label = "Cube")
    var selectedScore by remember { mutableStateOf<Pair<ScoreType, Int>?>(null) }

    Column(
        Modifier.fillMaxSize()
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.size(16.dp))
            players.forEachIndexed { index, player ->
                Text(
                    "Player ${index + 1}: ${player.scores.values.sum()}",
                    modifier = Modifier
                        .weight(1f, true)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.h6,
                    fontWeight = if (currentPlayer == index) FontWeight.Bold else FontWeight.Light
                )
            }
        }
        ScoreBoard(
            dices.map { it.number + 1 },
            players,
            currentPlayer,
            currentRoll,
            selectedScore?.first
        ) { score, value ->
            selectedScore = Pair(score, value)
        }
        // Dices
        Row(
            Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dices.forEach {
                Column {
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

                            AndroidView(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clickable {
                                        Toast
                                            .makeText(context, "Click", Toast.LENGTH_LONG)
                                            .show()
                                    },
                                factory = { context ->
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
                                }
                            )
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
                                contentDescription = "Face",
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            if (currentRoll > 0)
                                it.locked = !it.locked
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            if (it.locked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                            contentDescription = "Lock"
                        )
                    }
                }
            }
        }
        // Buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                currentRoll++
                dices.forEach {
                    if (it.locked) return@forEach
                    val random = Random(System.nanoTime()).nextInt(6, 36)
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
                players[currentPlayer].scores[selectedScore!!.first] = selectedScore!!.second
                calculateBonus(players[currentPlayer])
                if (currentPlayer == players.size - 1) {
                    currentPlayer = 0
                    currentRound++
                } else {
                    currentPlayer++
                }
                selectedScore = null
                dices.forEach { it.locked = false }

            }, enabled = selectedScore != null) {
                Text("Save Score")
            }
        }
        if (currentRound == 13) {
            AlertDialog(onDismissRequest = {},
                title = { Text("Game Finished") },
                text = {
                    if (numberOfPlayers == 1)
                        Text("You scored: ${players.first().scores.values.sum()} points")
                    else {
                        val scores = mutableListOf<Int>()
                        var winningScore = 0
                        players.forEachIndexed { index, player ->
                            val score = player.scores.values.sum()
                            scores.add(score)
                            if (score > winningScore) winningScore = score
                            Text("Player ${index + 1} scored ${player.scores.values.sum()}")
                        }
                        if (scores.distinct().size == 1) Text("Draw")
                        else Text("Player ${scores.indexOf(winningScore) + 1} won with $winningScore points")
                    }
                },
                confirmButton = {},
                dismissButton = {}
            )
        }
        BackHandler { ScreenRouter.navigateTo(Screens.Home) }
    }
}

@Composable
fun ScoreBoard(
    dices: List<Int>,
    players: List<Player>,
    currentPlayer: Int,
    currentRoll: Int,
    selectedScoreType: ScoreType?,
    selectScore: (ScoreType, Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(Modifier.weight(1f, true)) {
            ScoreHeader(players.size - 1)
            Score(dices, players, currentPlayer, ScoreType.One, currentRoll, selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Two, currentRoll ,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Three, currentRoll, selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Four, currentRoll ,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Five, currentRoll ,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Six, currentRoll, selectedScoreType, selectScore)
            BonusScore(players)
        }
        Column(Modifier.weight(1f, true)) {
            ScoreHeader(players.size - 1)
            Score(dices, players, currentPlayer, ScoreType.Tris, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Poker, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Full, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.SmallStraight, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.LargeStraight, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Yahtzee, currentRoll,selectedScoreType, selectScore)
            Score(dices, players, currentPlayer, ScoreType.Chance, currentRoll,selectedScoreType, selectScore)
        }
    }
}

@Composable
fun ScoreHeader(numberOfPlayers: Int) {
    Row(
        Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Type", modifier = Modifier
                .padding(4.dp)
                .weight(2f, true),
            textAlign = TextAlign.Center
        ) // TODO(translate)
        for (i in 0..numberOfPlayers) {
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .weight(1f, true),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("#${i + 1}")
            }
        }
    }
}

@Composable
fun BonusScore(players: List<Player>) {
    Row(
        Modifier
            .height(56.dp)
            .border(2.dp, Color.Black)
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Bonus", modifier = Modifier
                .padding(4.dp)
                .weight(2f, true),
            textAlign = TextAlign.Center
        ) // TODO(translate)
        players.forEach { player ->
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .weight(1f, true)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (player.bonusReached)
                    Icon(Icons.Outlined.Check, contentDescription = "Check")
                else Text("${player.lastSixScore}/63")
            }
        }
    }
}

@Composable
fun Score(
    dices: List<Int>,
    players: List<Player>,
    currentPlayer: Int,
    type: ScoreType,
    currentRoll: Int,
    selectedScoreType: ScoreType?,
    selectScore: (ScoreType, Int) -> Unit
) {
    Row(
        Modifier
            .height(56.dp)
            .border(2.dp, Color.Black)
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            type.name, modifier = Modifier
                .padding(4.dp)
                .weight(2f, true),
            textAlign = TextAlign.Center
        ) // TODO(translate)
        players.forEachIndexed { index, player ->
            var score = "0"
            val savedScore = player.scores[type]
            var count = 0
            if (savedScore == null) {
                if (currentRoll > 0) {
                    if (currentPlayer == index) {
                        count = calculateScore(dices, type)
                        score = count.toString()
                    }
                }
            } else score = savedScore.toString()
            val color = if (savedScore != null) Color.LightGray else Color.White
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .border(3.dp, if (selectedScoreType == type && index == currentPlayer) Color.Blue else Color.Black)
                    .background(color)
                    .weight(1f, true)
                    .fillMaxSize()
                    .clickable {
                        if (index == currentPlayer && savedScore == null && currentRoll > 0)
                            selectScore(type, count)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(score)
            }
        }
    }
}

enum class ScoreType { One, Two, Three, Four, Five, Six, Tris, Poker, Full, SmallStraight, LargeStraight, Chance, Yahtzee }

class Player {
    val scores = mutableMapOf<ScoreType, Int>()
    var bonusReached = false
    var lastSixScore = 0
}

fun calculateBonus(player: Player) {
    if (player.bonusReached) return
    val lastSix = player.scores.values.toList().takeLast(6)
    player.bonusReached = lastSix.sum() >= 63
    player.lastSixScore = lastSix.sum()
}

fun calculateScore(dices: List<Int>, type: ScoreType): Int {
    when (type) {
        ScoreType.One -> return dices.filter { dice -> dice == 1 }.size
        ScoreType.Two -> return dices.filter { dice -> dice == 2 }.size * 2
        ScoreType.Three -> return dices.filter { dice -> dice == 3 }.size * 3
        ScoreType.Four -> return dices.filter { dice -> dice == 4 }.size * 4
        ScoreType.Five -> return dices.filter { dice -> dice == 5 }.size * 5
        ScoreType.Six -> return dices.filter { dice -> dice == 6 }.size * 6
        ScoreType.Tris -> {
            if (dices.distinct().size > 3) return 0
            dices.distinct().forEach { value ->
                val subList = dices.filter { it == value }
                if (subList.size >= 3) return dices.sum()
            }
            return 0
        }
        ScoreType.Poker -> {
            if (dices.distinct().size > 2) return 0
            dices.distinct().forEach { value ->
                val subList = dices.filter { it == value }
                if (subList.size >= 4) return dices.sum()
            }
            return 0
        }
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
