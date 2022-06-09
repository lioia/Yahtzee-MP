package me.lioironzello.yahtzee.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import com.github.skgmn.composetooltip.AnchorEdge
import com.github.skgmn.composetooltip.Tooltip
import com.github.skgmn.composetooltip.rememberTooltipStyle
import com.google.ar.sceneform.rendering.RenderableInstance
import io.github.sceneview.SceneView
import io.github.sceneview.material.setBaseColor
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.utils.colorOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.database.GameDatabase
import me.lioironzello.yahtzee.model.DiceModel
import me.lioironzello.yahtzee.model.DiceVelocity
import me.lioironzello.yahtzee.model.Game
import me.lioironzello.yahtzee.model.SettingsModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.random.Random

@ExperimentalAnimationApi
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
                        colorOf(
                            settingsModel.diceColor.color.red,
                            settingsModel.diceColor.color.green,
                            settingsModel.diceColor.color.blue
                        )
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
            canvas.drawColor(settingsModel.diceColor.color.toArgb())
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

@ExperimentalAnimationApi
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
            DiceModel(is3D, faces).apply {
                setModel(
                    referenceModel,
                    settingsModel.diceColor.color
                )
            },
            DiceModel(is3D, faces).apply {
                setModel(
                    referenceModel,
                    settingsModel.diceColor.color
                )
            },
            DiceModel(is3D, faces).apply {
                setModel(
                    referenceModel,
                    settingsModel.diceColor.color
                )
            },
            DiceModel(is3D, faces).apply {
                setModel(
                    referenceModel,
                    settingsModel.diceColor.color
                )
            },
            DiceModel(is3D, faces).apply { setModel(referenceModel, settingsModel.diceColor.color) }
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
    var goBackDialogVisible by remember { mutableStateOf(false) }
    var yahtzeeAnimation by remember { mutableStateOf(false) }

    val delay = when (settingsModel.diceVelocity) {
        DiceVelocity.Slow -> 1000
        DiceVelocity.Medium -> 500
        DiceVelocity.Fast -> 0
    }

    val mediumSound = MediaPlayer.create(context, R.raw.medium)
    val slowSound = MediaPlayer.create(context, R.raw.slow)

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        Image(
            painter = painterResource(if (settingsModel.darkTheme) R.drawable.black else R.drawable.white),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
    Column(
        Modifier.fillMaxSize()
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            IconButton(onClick = { goBackDialogVisible = true }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.size(16.dp))
            players.forEachIndexed { index, player ->
                Text(
                    stringResource(
                        R.string.player_score,
                        index + 1
                    ) + ": ${player.scores.values.sum() + if (player.bonusReached) 35 else 0}",
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
            delay,
            {
                yahtzeeAnimation = true
                CoroutineScope(Dispatchers.Default).launch {
                    delay(1000)
                    yahtzeeAnimation = false
                }
            },
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
                    Dice(it, settingsModel.diceVelocity, transition)
                    IconButton(
                        onClick = { if (currentRoll > 0) it.locked = !it.locked },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Icon(
                            if (it.locked) Icons.Outlined.Lock else Icons.Outlined.LockOpen,
                            contentDescription = "Lock",
                            tint = if (it.locked) Color(0xFFFFA000) else MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        }
        // Buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                if (settingsModel.soundEnabled) {
                    if (settingsModel.diceVelocity == DiceVelocity.Slow) slowSound.start()
                    else mediumSound.start()
                }
                currentRoll++
                dices.forEach {
                    if (it.locked) return@forEach
                    val random = Random(System.nanoTime()).nextInt(6, 36)
                    it.number = random % 6
                    if (it.is3D) {
                        it.kx = DiceModel.Values3D[it.number].first + Random.nextInt(1, 5) * 4
                        it.ky = DiceModel.Values3D[it.number].second + Random.nextInt(1, 5) * 4
                    } else {
                        it.randomValue = random
                    }
                    animate = true
                }
            }, enabled = currentRoll < 3) {
                Text(stringResource(R.string.roll))
            }
            Button(onClick = {
                val score = selectedScore!!
                currentRoll = 0
                if (score.first == ScoreType.Yahtzee && score.second == 50)
                    players[currentPlayer].doubleYahtzee = true
                if (score.first != ScoreType.Yahtzee && score.second == 50)
                    players[currentPlayer].doubleYahtzee = false
                players[currentPlayer].scores[score.first] = score.second
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
                Text(stringResource(R.string.save_score))
            }
        }
        if (currentRound == 13) {
            val player1Score =
                players[0].scores.values.sum() + if (players[0].bonusReached) 35 else 0
            var player2Score: Int? = null
            if (players.size == 2) {
                player2Score =
                    players[1].scores.values.sum() + if (players[1].bonusReached) 35 else 0
            }

            AlertDialog(onDismissRequest = {},
                title = { Text("Game Finished") },
                text = {
                    if (numberOfPlayers == 1)
                        Text("You scored: $player1Score points")
                    else {
                        Column(Modifier.padding(16.dp)) {
                            Text("Player 1 scored $player1Score")
                            player2Score?.let {
                                Text("Player 2 scored $it")
                                if (player1Score == it) Text("Draw")
                                else if (player1Score > it) Text("Player 1 won with $player1Score points")
                                else Text("Player 2 won with $it points")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveGame(context, player1Score, player2Score)
                        }
                        dices.forEach {
                            it.locked = false
                            it.kx = 0
                            it.ky = 0
                            it.randomValue = 1
                            it.number = 1
                        }
                        currentRound = 0
                        currentRoll = 0
                        currentPlayer = 0
                        players.forEach {
                            it.bonusReached = false
                            it.lastSixScore = 0
                            it.scores = mutableMapOf()
                        }
                        selectedScore = null
                    }) {
                        Text("Play Again")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            saveGame(context, player1Score, player2Score)
                        }
                        ScreenRouter.navigateTo(Screens.Home)
                    }) {
                        Text("Go back")
                    }
                }
            )
        }
        AnimatedVisibility(yahtzeeAnimation, enter = scaleIn(), exit = scaleOut()) {
            Text(
                stringResource(R.string.yahtzee),
                style = MaterialTheme.typography.h1,
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold
            )
        }
        if (goBackDialogVisible) {
            AlertDialog(
                title = { Text("Are you sure you want to go back?") },
                text = { Text("If you go back, the progress will be lost") },
                onDismissRequest = { goBackDialogVisible = false },
                confirmButton = {
                    Button(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                        Text("Go Back")
                    }
                },
                dismissButton = {
                    Button(onClick = { goBackDialogVisible = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        BackHandler { goBackDialogVisible = true }
    }
}

@Composable
fun Dice(dice: DiceModel, diceVelocity: DiceVelocity, transition: Transition<Boolean>) {
    when (diceVelocity) {
        DiceVelocity.Slow -> {
            val rotation by transition.animateIntSize(
                label = "Cube",
                transitionSpec = { tween(durationMillis = 1000) }) { state ->
                if (state) IntSize(
                    dice.kx * 90,
                    dice.ky * 90
                ) else IntSize(
                    dice.modelNode.modelRotation.x.toInt(),
                    dice.modelNode.modelRotation.y.toInt()
                )
            }
            dice.modelNode.modelRotation = Rotation(
                rotation.width.toFloat(),
                rotation.height.toFloat(), 90f
            )
            val bgColor = MaterialTheme.colors.background

            AndroidView(
                modifier = Modifier.size(80.dp),
                factory = { context ->
                    SceneView(context).apply {
                        mainLight = null
                        backgroundColor =
                            colorOf(bgColor.red, bgColor.green, bgColor.blue)
                        addChild(dice.modelNode)
                        gestureDetector.moveGestureDetector = null
                        gestureDetector.rotateGestureDetector = null
                        gestureDetector.scaleGestureDetector = null
                        gestureDetector.cameraManipulator = null
                    }
                }
            )
        }
        else -> {
            val value = if (diceVelocity == DiceVelocity.Medium) {
                transition.animateInt(
                    label = "Cube",
                    transitionSpec = { tween(durationMillis = 500) }) { state ->
                    if (state) dice.randomValue else dice.number
                }.value
            } else {
                dice.randomValue
            }

            Image(
                dice.faces[value % 6].asImageBitmap(),
                contentDescription = "Face",
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

fun saveGame(context: Context, player1Score: Int, player2Score: Int?) {
    val db = GameDatabase.getInstance(context)
    val dao = db.gameDao()

    val date = LocalDate.now()
    val time = LocalTime.now()
    val zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())

    val game = Game(
        date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(zonedDateTime),
        player1Score = player1Score,
        player2Score = player2Score
    )
    dao.insert(game)
}

@Composable
fun ScoreBoard(
    dices: List<Int>,
    players: List<Player>,
    currentPlayer: Int,
    currentRoll: Int,
    delay: Int,
    updateYahtzee: () -> Unit,
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
            ScoreType.values().take(6).forEach {
                Score(
                    dices,
                    players,
                    currentPlayer,
                    it,
                    currentRoll,
                    delay,
                    updateYahtzee,
                    selectedScoreType,
                    selectScore
                )
            }
            BonusScore(players, currentPlayer)
        }
        Column(Modifier.weight(1f, true)) {
            ScoreHeader(players.size - 1)
            ScoreType.values().takeLast(7).forEach {
                Score(
                    dices,
                    players,
                    currentPlayer,
                    it,
                    currentRoll,
                    delay,
                    updateYahtzee,
                    selectedScoreType,
                    selectScore
                )
            }
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
            stringResource(R.string.type), modifier = Modifier
                .padding(4.dp)
                .weight(2f, true),
            textAlign = TextAlign.Center
        )
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
fun BonusScore(players: List<Player>, currentPlayer: Int) {
    Row(
        Modifier
            .height(56.dp)
            .border(2.dp, MaterialTheme.colors.onBackground)
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var tooltipVisible by remember { mutableStateOf(false) }
        val tooltipStyle = rememberTooltipStyle().apply {
            color = MaterialTheme.colors.background
            tipWidth = 0.dp
            tipHeight = 0.dp
        }
        Box(
            Modifier
                .weight(2f, true)
                .clickable {
                    tooltipVisible = true
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(3000)
                        tooltipVisible = false
                    }

                }) {
            Text(
                stringResource(R.string.bonus), modifier = Modifier
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )
            if (tooltipVisible)
                Tooltip(
                    anchorEdge = AnchorEdge.Top,
                    tooltipStyle = tooltipStyle,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        stringResource(R.string.tutorial_bonus),
                        style = MaterialTheme.typography.caption
                    )
                }
        }
        players.forEachIndexed { index, player ->
            Column(
                modifier = Modifier
                    .padding(1.dp)
                    .weight(1f, true)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (player.bonusReached)
                    Icon(Icons.Outlined.Check, contentDescription = "Check")
                else Text(
                    "${player.lastSixScore}/63",
                    fontWeight = if (currentPlayer == index) FontWeight.Bold else FontWeight.Light
                )
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
    delay: Int,
    updateYahtzee: () -> Unit,
    selectedScoreType: ScoreType?,
    selectScore: (ScoreType, Int) -> Unit
) {
    var tooltipVisible by remember { mutableStateOf(false) }
    val tooltipStyle = rememberTooltipStyle().apply {
        color = MaterialTheme.colors.background
        tipWidth = 0.dp
        tipHeight = 0.dp
    }

    Row(
        Modifier
            .height(56.dp)
            .border(2.dp, MaterialTheme.colors.onBackground)
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .weight(2f, true)
                .clickable {
                    tooltipVisible = true
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(3000)
                        tooltipVisible = false
                    }
                }) {
            Text(
                stringResource(type.text), modifier = Modifier
                    .padding(4.dp),
                textAlign = TextAlign.Center
            )
            if (tooltipVisible)
                Tooltip(
                    anchorEdge = AnchorEdge.Top,
                    tooltipStyle = tooltipStyle,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(stringResource(type.helperText), style = MaterialTheme.typography.caption)
                }
        }
        players.forEachIndexed { index, player ->
            var score = "0"
            val savedScore = player.scores[type]
            var count = 0
            if (savedScore == null) {
                if (currentRoll > 0 && currentPlayer == index) {
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(delay.toLong())
                        count = calculateScore(dices, type, players[currentPlayer].doubleYahtzee)
                        score = count.toString()
                        if (count == 50) updateYahtzee()
                    }
                }
            } else score = savedScore.toString()
            val color = if (savedScore != null) Color.Gray else MaterialTheme.colors.background
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .border(
                        3.dp,
                        if (selectedScoreType == type && index == currentPlayer) Color.Blue else MaterialTheme.colors.onBackground
                    )
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

enum class ScoreType(val text: Int, val helperText: Int) {
    One(R.string.one, R.string.tutorial_one_to_six),
    Two(R.string.two, R.string.tutorial_one_to_six),
    Three(R.string.three, R.string.tutorial_one_to_six),
    Four(R.string.four, R.string.tutorial_one_to_six),
    Five(R.string.five, R.string.tutorial_one_to_six),
    Six(R.string.six, R.string.tutorial_one_to_six),
    Tris(R.string.tris, R.string.tutorial_tris_poker),
    Poker(R.string.poker, R.string.tutorial_tris_poker),
    Full(R.string.full, R.string.tutorial_full),
    SmallStraight(R.string.small_straight, R.string.tutorial_small_straight),
    LargeStraight(R.string.large_straight, R.string.tutorial_large_straight),
    Yahtzee(R.string.yahtzee, R.string.tutorial_yahtzee),
    Chance(R.string.chance, R.string.tutorial_chance)
}

class Player {
    var scores = mutableMapOf<ScoreType, Int>()
    var bonusReached = false
    var lastSixScore = 0
    var doubleYahtzee = false
}

fun calculateBonus(player: Player) {
    if (player.bonusReached) return
    val lastSix = player.scores.values.toList().takeLast(6)
    player.bonusReached = lastSix.sum() >= 63
    player.lastSixScore = lastSix.sum()
}

fun calculateScore(dices: List<Int>, type: ScoreType, doubleYahtzee: Boolean): Int {
    if (doubleYahtzee && dices.distinct().size == 1) return 50
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
