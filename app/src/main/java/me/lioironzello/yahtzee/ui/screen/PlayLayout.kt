package me.lioironzello.yahtzee.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import com.github.skgmn.composetooltip.AnchorEdge
import com.github.skgmn.composetooltip.Tooltip
import com.github.skgmn.composetooltip.rememberTooltipStyle
import com.google.ar.sceneform.rendering.RenderableInstance
import com.google.gson.Gson
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
import me.lioironzello.yahtzee.database.GameRepository
import me.lioironzello.yahtzee.model.*
import me.lioironzello.yahtzee.model.DiceModel.Companion.Values3D
import java.io.File
import java.nio.charset.Charset
import kotlin.random.Random

// Function used to load the page
@ExperimentalAnimationApi
@Composable
fun PlayLayout(settingsModel: SettingsModel, numberOfPlayers: Int, continueGame: Boolean) {
    // 3d model
    var referenceModel by remember { mutableStateOf<RenderableInstance?>(null) }
    // 2d images created at runtime
    var faces = listOf<Bitmap>()

    val context = LocalContext.current

    // Loading 3D model only on slow dice velocity
    if (settingsModel.diceVelocity == DiceVelocity.Slow) {
        val lifecycleOwner = LocalLifecycleOwner.current

        // Code from https://github.com/SceneView/sceneview-android/blob/main/samples/model-viewer/src/main/java/io/github/sceneview/sample/modelviewer/MainFragment.kt#L48
        lifecycleOwner.lifecycleScope.launchWhenCreated {
            if (referenceModel == null) {
                referenceModel = ModelNode().loadModel(
                    context = context,
                    lifecycle = lifecycleOwner.lifecycle,
                    glbFileLocation = "cube.glb",
                    centerOrigin = Position(x = 0.0f, y = 0.0f, z = 0.0f)
                )
                // Changing the model settings only after it loaded
                referenceModel?.let {
                    // Changing the dice color
                    // The 3d model has a material named "background"
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
    } else { // Creating the 2d images if the dice velocity is not slow
        // The density is used to have the image dimensions related to the device size
        val density = context.resources.displayMetrics.density
        val size = 64 * density // bitmap size
        val radius = 64 / 14 * density // circle radius
        // Used to draw the circles
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
            canvas.drawColor(settingsModel.diceColor.color.toArgb()) // background color of the dice
            // Black border lines
            canvas.drawLine(0f, 0f, 0f, size, paint)
            canvas.drawLine(0f, 0f, size, 0f, paint)
            canvas.drawLine(size, 0f, size, size, paint)
            canvas.drawLine(0f, size, size, size, paint)
            // Adding the circles based on the face index
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

    val gameState = if (continueGame) {
        val file = File(context.cacheDir, "state")
        val json = file.readText()
        val gson = Gson()
        gson.fromJson(json, GameState::class.java)
    } else null

    // Loading the Play function with the correct information
    if (settingsModel.diceVelocity == DiceVelocity.Slow) {
        if (referenceModel != null) {
            Play(settingsModel, numberOfPlayers, referenceModel, faces, gameState)
        } else { // Model is not loaded
            Column(
                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(stringResource(R.string.loading))
            }
        }
    } else {
        Play(settingsModel, numberOfPlayers, null, faces, gameState)
    }
}

@ExperimentalAnimationApi
@Composable
fun Play(
    settingsModel: SettingsModel,
    numPlayers: Int,
    referenceModel: RenderableInstance?,
    faces: List<Bitmap>,
    gameState: GameState?
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Utility variable used by the Dice class
    val is3D = settingsModel.diceVelocity == DiceVelocity.Slow
    val diceColor = settingsModel.diceColor.color
    // List of 5 dices
    val dices = rememberSaveable {
        mutableStateOf(
            listOf(
                DiceModel(is3D, faces).apply {
                    init(
                        referenceModel,
                        diceColor,
                        gameState?.dices?.get(0) ?: 1
                    )
                },
                DiceModel(is3D, faces).apply {
                    init(
                        referenceModel,
                        diceColor,
                        gameState?.dices?.get(1) ?: 1
                    )
                },
                DiceModel(is3D, faces).apply {
                    init(
                        referenceModel,
                        diceColor,
                        gameState?.dices?.get(2) ?: 1
                    )
                },
                DiceModel(is3D, faces).apply {
                    init(
                        referenceModel,
                        diceColor,
                        gameState?.dices?.get(3) ?: 1
                    )
                },
                DiceModel(is3D, faces).apply {
                    init(
                        referenceModel,
                        diceColor,
                        gameState?.dices?.get(4) ?: 1
                    )
                },
            )
        )
    }

    // Reload game state from previous saved game
    val numberOfPlayers = gameState?.players?.size ?: numPlayers
    val player1 = gameState?.players?.get(0) ?: Player()
    var player2 = Player()
    if (gameState != null && numberOfPlayers > 1)
        player2 = gameState.players[1]

    // Variables for the controlling the game
    val currentRound =
        rememberSaveable { mutableStateOf(gameState?.players?.last()?.scores?.size ?: 0) }
    val currentRoll = rememberSaveable { mutableStateOf(gameState?.currentRoll ?: 0) }
    val currentPlayer =
        rememberSaveable { mutableStateOf(if (player1.scores.size == player2.scores.size + 1) 1 else 0) }
    val players = if (numberOfPlayers == 1) {
        remember { mutableStateListOf(player1) }
    } else {
        remember { mutableStateListOf(player1, player2) }
    }
    var selectedScore by remember { mutableStateOf<Pair<ScoreType, Int>?>(null) }

    // Used to trigger the dice rolling animation after clicking the Roll button
    val animate = rememberSaveable { mutableStateOf(false) }
    // Triggered by the "animate" variable used to control the animation
    val transition = updateTransition(targetState = animate, label = "Cube")
    // Buttons are disabled when animating
    val buttonEnabled = rememberSaveable { mutableStateOf(true) }


    // Variable used to update the scores after the animation finishes
    val waitTime = when (settingsModel.diceVelocity) {
        DiceVelocity.Slow -> 1000
        DiceVelocity.Medium -> 500
        DiceVelocity.Fast -> 100
    }

    // Two different sounds for different velocity
    val mediumSound = MediaPlayer.create(context, R.raw.medium)
    val slowSound = MediaPlayer.create(context, R.raw.slow)

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        // Background image
        Image(
            painter = painterResource(if (settingsModel.darkTheme) R.drawable.black else R.drawable.white),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
    }
    Column(Modifier.fillMaxSize()) {
        // Header row
        Row(
            Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                val state = GameState(dices.value.map { it.number }, currentRoll.value, players)
                saveState(context, state)
                ScreenRouter.navigateTo(Screens.Home)
            }) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Spacer(Modifier.size(16.dp))
            players.forEachIndexed { index, player ->
                Text(
                    stringResource(
                        R.string.player_index,
                        index + 1
                    ) + ": ${player.scores.values.sum() + if (player.bonusReached) 35 else 0}",
                    modifier = Modifier
                        .weight(1f, true)
                        .align(Alignment.CenterVertically),
                    style = MaterialTheme.typography.h6,
                    fontWeight = if (currentPlayer.value == index) FontWeight.Bold else FontWeight.Light
                )
            }
        }
        Column(Modifier.weight(1f, true)) {
            ScoreBoard(
                dices.value.map { it.number + 1 },
                players,
                currentPlayer.value,
                currentRoll.value,
                waitTime,
                selectedScore?.first
            ) { score, value ->
                selectedScore = Pair(score, value)
            }
        }
        // Dices
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dices.value.forEach {
                Column {
                    Dice(it, settingsModel.diceVelocity, transition)
                    IconButton(
                        onClick = { if (currentRoll.value > 0) it.locked = !it.locked },
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
        // Roll Button enabled only if the number of rolls is less than 3 (starting from 0)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {
                // Play the sound if it's enabled
                if (settingsModel.soundEnabled) {
                    if (settingsModel.diceVelocity == DiceVelocity.Slow) slowSound.start()
                    else mediumSound.start()
                }
                currentRoll.value++
                dices.value.forEach {
                    // Skip the dice if it's locked
                    if (it.locked) return@forEach
                    // Select a random value
                    it.number = Random(System.nanoTime()).nextInt(0, 6)
                    // Update variables used for animation in the Dice class
                    if (it.is3D) {
                        // Setting the kx and ky variable to the values from the Values3D utility array (rotation values on the x and y axis)
                        // Then it adds a random value multiplied by 4 to have multiple spins
                        // 4 represents a complete spin of the dice
                        it.kx =
                            Values3D[it.number].first + Random(System.nanoTime()).nextInt(
                                1,
                                10
                            ) * 4
                        it.ky = Values3D[it.number].second + Random(System.nanoTime()).nextInt(
                            1,
                            10
                        ) * 4
                    } else {
                        // Adding a random value multiplied by 6 to the selected number to have multiple spins
                        // 6 represents a complete spin of the dice
                        it.randomValue = it.number + Random(System.nanoTime()).nextInt(1, 10) * 6
                    }
                    // Enabling animation
                    animate.value = true

                    // Disable button
                    buttonEnabled.value = false
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(waitTime.toLong())
                        buttonEnabled.value = true
                    }
                }
            }, enabled = currentRoll.value < 3 && buttonEnabled.value) {
                Text(stringResource(R.string.roll))
            }
            // Save score button (enabled only if selectedScore is null)
            Button(onClick = {
                // This value is not null (otherwise the button would be disabled)
                val score = selectedScore!!
                // Enabling the possibility to make another Yahtzee after the first one
                if (score.first == ScoreType.Yahtzee && score.second == 50)
                    players[currentPlayer.value].doubleYahtzee = true
                // Disabling double yahtzee after the second yahtzee
                if (score.first != ScoreType.Yahtzee && score.second == 50)
                    players[currentPlayer.value].doubleYahtzee = false
                // Save the score in the player map
                players[currentPlayer.value].scores[score.first] = score.second
                // Calculate bonus with the new saved score
                players[currentPlayer.value].calculateBonus()
                // Current Player is the last player
                if (currentPlayer.value == players.size - 1) {
                    // Switching player
                    currentPlayer.value = 0
                    // Go to next round
                    currentRound.value++
                } else {
                    // Go to next player
                    currentPlayer.value++
                }
                // Resetting variables
                currentRoll.value = 0
                selectedScore = null
                dices.value.forEach { it.locked = false }

            }, enabled = selectedScore != null && buttonEnabled.value) {
                Text(stringResource(R.string.save_score))
            }
        }

        if (currentRound.value == 13)
            GameFinished(players) {
                // Resetting the variables used to control the game
                dices.value.forEach {
                    it.locked = false
                    it.kx = 0
                    it.ky = 0
                    it.randomValue = 1
                    it.number = 1
                }
                currentRound.value = 0
                currentRoll.value = 0
                currentPlayer.value = 0
                players.forEach {
                    it.bonusReached = false
                    it.lastSixScore = 0
                    it.scores = mutableMapOf()
                }
                selectedScore = null
            }
        BackHandler {
            val state = GameState(dices.value.map { it.number }, currentRoll.value, players)
            saveState(context, state)
            ScreenRouter.navigateTo(Screens.Home)
        }
        // Side-effect to release sound resources and to listen to lifecycle changes
        DisposableEffect(lifecycleOwner) {
            // Observing lifecycle changes
            val observer = LifecycleEventObserver { _, event ->
                // Saving game state
                if (event == Lifecycle.Event.ON_STOP) {
                    // Creating an object representing the game state
                    val state = GameState(dices.value.map { it.number }, currentRoll.value, players)
                    saveState(context, state)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                mediumSound.release()
                slowSound.release()
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }
}

fun saveState(context: Context, state: GameState) {
    val file = File(context.cacheDir, "state")
    val gson = Gson()
    val jsonString = gson.toJson(state)
    // Creating the new state file in the cache directory
    file.writeText(jsonString, Charset.defaultCharset())
}

@Composable
fun Dice(
    dice: DiceModel,
    diceVelocity: DiceVelocity,
    transition: Transition<MutableState<Boolean>>
) {
    when (diceVelocity) {
        // Showing the 3d model if the velocity is slow
        DiceVelocity.Slow -> {
            // State changed only if the "animate" variable (in Play) is true
            // The animation is based on changing the rotation of the model
            val rotation by transition.animateIntSize(
                label = "Cube",
                transitionSpec = { tween(durationMillis = 1000) }) { state ->
                if (state.value) IntSize(
                    dice.kx * 90,
                    dice.ky * 90
                ) else IntSize(
                    dice.modelNode.rotation.x.toInt(),
                    dice.modelNode.rotation.y.toInt()
                )
            }
            dice.modelNode.rotation =
                Rotation(rotation.width.toFloat(), rotation.height.toFloat(), 90f)
            val bgColor = MaterialTheme.colors.background

            // Calling AndroidView to render a non-JetpackCompose layout
            AndroidView(
                modifier = Modifier.size(80.dp),
                factory = { context ->
                    SceneView(context).apply {
                        mainLight = null
                        backgroundColor =
                            colorOf(bgColor.red, bgColor.green, bgColor.blue)
                        addChild(dice.modelNode)
                        // Disabling dice input
                        gestureDetector.moveGestureDetector = null
                        gestureDetector.rotateGestureDetector = null
                        gestureDetector.scaleGestureDetector = null
                        gestureDetector.cameraManipulator = null
                    }
                },
            )
        }
        // Showing the 2d images if the velocity is not slow
        else -> {
            // Animating only on medium velocity
            val value = if (diceVelocity == DiceVelocity.Medium) {
                // Animation is based on changing the index of the image to show
                transition.animateInt(
                    label = "Cube",
                    transitionSpec = { tween(durationMillis = 500) }) { state ->
                    if (state.value) dice.randomValue else dice.number
                }.value
            } else {
                // No animation on fast velocity
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

@Composable
fun GameFinished(players: List<Player>, resetGame: () -> Unit) {
    val context = LocalContext.current

    // Loading the database
    val db = GameDatabase.getInstance(context)
    val dao = db.gameDao()
    val repository = GameRepository(dao)

    // Calculate the final scores
    val player1Score =
        players[0].scores.values.sum() + if (players[0].bonusReached) 35 else 0
    var player2Score: Int? = null
    if (players.size == 2) {
        player2Score =
            players[1].scores.values.sum() + if (players[1].bonusReached) 35 else 0
    }

    AlertDialog(onDismissRequest = {},
        title = { Text(stringResource(R.string.game_finished)) },
        text = {
            // Showing different texts based on the number of players
            if (players.size == 1)
                Text(stringResource(R.string.your_score, player1Score))
            else {
                Column(Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.player_score, 1, player1Score))
                    player2Score?.let {
                        Text(stringResource(R.string.player_score, 2, it))
                        if (player1Score == it) Text(stringResource(R.string.draw))
                        else if (player1Score > it) Text(
                            stringResource(
                                R.string.player_won,
                                1,
                                player1Score
                            )
                        )
                        else Text(stringResource(R.string.player_won, 2, it))
                    }
                }
            }
        },
        // Saving the game in the database after clicking the buttons
        confirmButton = {
            // Play Again button
            Button(onClick = {
                repository.saveGame(player1Score, player2Score)
                // Reset the game board
                resetGame()
                File(context.cacheDir, "state").delete()
            }) {
                Text(stringResource(R.string.play_again))
            }
        },
        dismissButton = {
            // Go Back button
            Button(onClick = {
                repository.saveGame(player1Score, player2Score)
                // Return home
                ScreenRouter.navigateTo(Screens.Home)
                File(context.cacheDir, "state").delete()
            }) {
                Text(stringResource(R.string.go_back))
            }
        }
    )
}

@Composable
fun ScoreBoard(
    dices: List<Int>,
    players: List<Player>,
    currentPlayer: Int,
    currentRoll: Int,
    delay: Int,
    selectedScoreType: ScoreType?,
    selectScore: (ScoreType, Int) -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(Modifier.weight(1f, true)) {
            // Showing the first six score type on the left side (number scores) and the bonus score
            ScoreType.values().take(6).forEach {
                Row(Modifier.weight(1f, true)) {
                    Score(
                        dices,
                        players,
                        currentPlayer,
                        it,
                        currentRoll,
                        delay.toLong(),
                        selectedScoreType,
                        selectScore
                    )
                }
            }
            Row(Modifier.weight(1f, true)) {
                BonusScore(players, currentPlayer)
            }
        }
        Column(Modifier.weight(1f, true)) {
            // Showing the last 7 on the right side
            ScoreType.values().takeLast(7).forEach {
                Row(Modifier.weight(1f, true)) {
                    Score(
                        dices,
                        players,
                        currentPlayer,
                        it,
                        currentRoll,
                        delay.toLong(),
                        selectedScoreType,
                        selectScore
                    )
                }
            }
        }
    }
}

@Composable
fun BonusScore(players: List<Player>, currentPlayer: Int) {
    Row(
        Modifier
            .border(2.dp, MaterialTheme.colors.onBackground)
            .padding(4.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Variable used to show a help dialog
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
                    .padding(4.dp)
                    .align(Alignment.Center),
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
                // Showing a check icon if the bonus is reached, otherwise show the number of points scored
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
    updateDelay: Long,
    selectedScoreType: ScoreType?,
    selectScore: (ScoreType, Int) -> Unit
) {
    // Variable to show an help dialog
    var tooltipVisible by remember { mutableStateOf(false) }
    val tooltipStyle = rememberTooltipStyle().apply {
        color = MaterialTheme.colors.background
        tipWidth = 0.dp
        tipHeight = 0.dp
    }

    Row(
        Modifier
            .border(2.dp, MaterialTheme.colors.onBackground)
            .padding(2.dp)
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
                    .padding(4.dp)
                    .align(Alignment.Center),
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
            // Getting the score from the player map
            val savedScore = player.scores[type]
            val count = rememberSaveable { mutableStateOf(0) }
            if (savedScore == null) { // The score was not in the player map
                if (currentRoll > 0 && currentPlayer == index) { // update the score after the first roll only if the index is the current player
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(updateDelay) // delay to finish the animation
                        count.value =
                            calculateScore(dices, type, players[currentPlayer].doubleYahtzee)
                    }
                } else count.value = 0 // it's not the current player
            } else count.value = savedScore // savedScore is not null
            // Change the background color only if the score was already saved
            val color = if (savedScore != null) Color.Gray else MaterialTheme.colors.background
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .border(
                        3.dp,
                        // Apply a border to the selected score
                        if (selectedScoreType == type && index == currentPlayer) Color.Blue else MaterialTheme.colors.onBackground
                    )
                    .background(color)
                    .weight(1f, true)
                    .fillMaxSize()
                    .clickable {
                        // Select score only if it's the current player turn and it's after the first roll
                        if (index == currentPlayer && savedScore == null && currentRoll > 0)
                            selectScore(type, count.value)
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(count.value.toString())
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

// Utility function used to calculate the score
fun calculateScore(dices: List<Int>, type: ScoreType, doubleYahtzee: Boolean): Int {
    if (doubleYahtzee && dices.distinct().size == 1) return 50
    when (type) {
        // The return value is the number of equal dice for the i-th face multiplied by i
        ScoreType.One -> return dices.filter { dice -> dice == 1 }.size
        ScoreType.Two -> return dices.filter { dice -> dice == 2 }.size * 2
        ScoreType.Three -> return dices.filter { dice -> dice == 3 }.size * 3
        ScoreType.Four -> return dices.filter { dice -> dice == 4 }.size * 4
        ScoreType.Five -> return dices.filter { dice -> dice == 5 }.size * 5
        ScoreType.Six -> return dices.filter { dice -> dice == 6 }.size * 6
        ScoreType.Tris -> {
            if (dices.distinct().size > 3) return 0 // it's not a tris
            // Check if the size of sublist of the same value is more or equal than 3
            dices.distinct().forEach { value ->
                val subList = dices.filter { it == value }
                if (subList.size >= 3) return dices.sum()
            }
            return 0
        }
        // Similar code to the tris
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
            // subList1 is all the values equal to the first
            val subList1 = dices.filter { dice -> dice == first }
            // subList2 is dices - subList1
            val subList2 = dices.filter { dice -> dice != first }
            if (subList2.distinct().size != 1) return 0 // the values are different
            // The possibilities are:
            // subList1 has size 2 => subList2 has size 3
            // subList1 has size 3 => subList2 has size 2
            if (subList1.size >= 2 && subList2.size >= 2) return 25
            return 0
        }
        ScoreType.SmallStraight -> {
            // Checking all the three different cases
            // 1, 2, 3, 4
            // 2, 3, 4, 5
            // 3, 4, 5, 6
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
        // Checking if the dices are all different and the sum is the 15 (case: 1, 2, 3, 4, 5) or 20 (case: 2, 3, 4, 5, 6)
        ScoreType.LargeStraight -> return if (dices.distinct().size == 5 && (dices.sum() == 15 || dices.sum() == 20)) 40 else 0
        // Returning the sum
        ScoreType.Chance -> return dices.sum()
        // Checking if all the dices are equal
        ScoreType.Yahtzee -> return if (dices.distinct().size == 1) 50 else 0
    }
}
