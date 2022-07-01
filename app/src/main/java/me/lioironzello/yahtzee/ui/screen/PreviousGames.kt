package me.lioironzello.yahtzee.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.database.GameDatabase
import me.lioironzello.yahtzee.database.GameRepository
import me.lioironzello.yahtzee.model.Game

@Composable
fun PreviousGames() {
    val context = LocalContext.current

    // Loading the database
    val db = GameDatabase.getInstance(context)
    val dao = db.gameDao()
    val repository = GameRepository(dao)

    // Getting all the saved games in the database
    val games by repository.allGames.observeAsState(listOf())
    // Filtering the games
    val singlePlayerGames = games.filter { it.player2Score == null }
    val multiPlayerGames = games - singlePlayerGames.toSet()

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(stringResource(R.string.previous_games)) },
            navigationIcon = {
                IconButton(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
        Table(1, singlePlayerGames.reversed())
        Table(2, multiPlayerGames.reversed())
        BackHandler { ScreenRouter.navigateTo(Screens.Home) }
    }
}

// Utility function for code reusability
@Composable
fun Table(numberOfPlayers: Int, games: List<Game>) {
    // Using LazyColumn for efficiency
    LazyColumn(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Table header
        item {
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.date), modifier = Modifier
                        .weight(1f, true)
                        .height(64.dp)
                        .border(2.dp, MaterialTheme.colors.onBackground)
                        .padding(8.dp)
                )
                for (i in 1..numberOfPlayers) {
                    Text(
                        stringResource(R.string.player_index, i), modifier = Modifier
                            .weight(1f, true)
                            .height(64.dp)
                            .border(2.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                }
            }
        }
        // Displaying the games
        items(games.size) { index ->
            val game = games[index]
            Row(
                Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    game.date, modifier = Modifier
                        .weight(1f, true)
                        .height(60.dp)
                        .border(1.dp, MaterialTheme.colors.onBackground)
                        .padding(8.dp)
                )
                Text(
                    game.player1Score.toString(), modifier = Modifier
                        .weight(1f, true)
                        .height(60.dp)
                        .border(1.dp, MaterialTheme.colors.onBackground)
                        .padding(8.dp)
                )
                if (game.player2Score != null)
                    Text(
                        game.player2Score.toString(), modifier = Modifier
                            .weight(1f, true)
                            .height(60.dp)
                            .border(1.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
            }
        }
    }
}
