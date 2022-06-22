package me.lioironzello.yahtzee.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.lioironzello.yahtzee.model.Game
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class GameRepository(private val daoGame: GameDao) {
    val allGames: LiveData<List<Game>> = daoGame.loadAll()

    fun saveGame(player1Score: Int, player2Score: Int?) {
        val date = LocalDate.now()
        val time = LocalTime.now()
        val zonedDateTime = ZonedDateTime.of(date, time, ZoneId.systemDefault())

        val game = Game(
            date = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).format(zonedDateTime),
            player1Score = player1Score,
            player2Score = player2Score
        )
        CoroutineScope(Dispatchers.IO).launch {
            daoGame.insert(game)
        }
    }
}
