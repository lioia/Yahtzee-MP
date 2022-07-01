package me.lioironzello.yahtzee.model

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import me.lioironzello.yahtzee.ui.screen.ScoreType
import me.lioironzello.yahtzee.ui.screen.calculateScore

@Parcelize
class GameState(private val numberOfPlayers: Int, private val is3D: Boolean) :
    Parcelable {
    fun restore(lastGameState: LastGameState? = null) {
        // Reload game state from previous saved game
        if (lastGameState != null) {
            dices.forEachIndexed { index, dice ->
                dice.number = lastGameState.dices[index]
                dice.updateValues()
            }
            val player1 = lastGameState.players[0]
            players[0].scores = player1.scores
            players[0].bonusReached = player1.bonusReached
            players[0].doubleYahtzee = player1.doubleYahtzee
            players[0].lastSixScore = player1.lastSixScore
            if (numberOfPlayers > 1) {
                val player2 = lastGameState.players[1]
                players[1].scores = player2.scores
                players[1].bonusReached = player2.bonusReached
                players[1].doubleYahtzee = player2.doubleYahtzee
                players[1].lastSixScore = player2.lastSixScore
            }
            currentPlayer =
                if (lastGameState.players.size > 1 && lastGameState.players[0].scores.size == lastGameState.players[1].scores.size + 1) 1
                else 0

            currentRoll = lastGameState.currentRoll

            scores.forEachIndexed { index, playerScore ->
                ScoreType.values().forEach {
                    val savedScore = players[index].scores[it]
                    val score =
                        if (currentPlayer == index && currentRoll > 0 && savedScore == null) calculateScore(
                            dices.map { dice -> dice.number + 1 },
                            it,
                            players[index].doubleYahtzee
                        ) else savedScore ?: 0
                    playerScore[it] = score
                }
            }
            currentRound = lastGameState.players.last().scores.size
        }
    }

    @IgnoredOnParcel
    private var _dices = mutableStateOf(
        listOf(
            DiceModel(is3D, 1),
            DiceModel(is3D, 1),
            DiceModel(is3D, 1),
            DiceModel(is3D, 1),
            DiceModel(is3D, 1)
        )
    )
    var dices: List<DiceModel>
        get() = _dices.value
        set(value) {
            _dices.value = value
        }

    @IgnoredOnParcel
    private var _scores: MutableState<List<MutableMap<ScoreType, Int>>> = mutableStateOf(
        if (numberOfPlayers == 1) listOf(mutableMapOf())
        else listOf(mutableMapOf(), mutableMapOf())
    )
    var scores: List<MutableMap<ScoreType, Int>>
        get() = _scores.value
        set(value) {
            _scores.value = value
        }

    // Variables for the controlling the game
    @IgnoredOnParcel
    private var _currentRound = mutableStateOf(0)
    var currentRound: Int
        get() = _currentRound.value
        set(value) {
            _currentRound.value = value
        }

    @IgnoredOnParcel
    private var _currentRoll = mutableStateOf(0)
    var currentRoll: Int
        get() = _currentRoll.value
        set(value) {
            _currentRoll.value = value
        }

    @IgnoredOnParcel
    private var _currentPlayer = mutableStateOf(0)
    var currentPlayer: Int
        get() = _currentPlayer.value
        set(value) {
            _currentPlayer.value = value
        }

    @IgnoredOnParcel
    private var _players =
        mutableStateOf(if (numberOfPlayers == 1) listOf(Player()) else listOf(Player(), Player()))
    var players: List<Player>
        get() = _players.value
        set(value) {
            _players.value = value
        }

    @IgnoredOnParcel
    private var _selectedScore = mutableStateOf<Pair<ScoreType, Int>?>(null)
    var selectedScore: Pair<ScoreType, Int>?
        get() = _selectedScore.value
        set(value) {
            _selectedScore.value = value
        }

    // Used to trigger the dice rolling animation after clicking the Roll button
    @IgnoredOnParcel
    private var _animate = mutableStateOf(false)
    var animate: Boolean
        get() = _animate.value
        set(value) {
            _animate.value = value
        }

    // Buttons are disabled when animating
    @IgnoredOnParcel
    private var _buttonEnabled = mutableStateOf(true)
    var buttonEnabled: Boolean
        get() = _buttonEnabled.value
        set(value) {
            _buttonEnabled.value = value
        }
}
