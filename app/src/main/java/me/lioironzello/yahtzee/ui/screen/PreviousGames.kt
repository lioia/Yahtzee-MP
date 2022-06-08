package me.lioironzello.yahtzee.ui.screen

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
import androidx.compose.ui.unit.dp
import me.lioironzello.yahtzee.database.GameDatabase

@Composable
fun PreviousGames() {
    val context = LocalContext.current

    val db = GameDatabase.getInstance(context)
    val dao = db.gameDao()
    val games by dao.loadAll().observeAsState(listOf())

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Previous Games") },
            navigationIcon = {
                IconButton(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
        LazyColumn(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        "Date", modifier = Modifier
                            .weight(1f, true)
                            .height(64.dp)
                            .border(2.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                    Text(
                        "Player 1 Score", modifier = Modifier
                            .weight(1f, true)
                            .height(64.dp)
                            .border(2.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                    Text(
                        "Player 2 Score", modifier = Modifier
                            .weight(1f, true)
                            .height(64.dp)
                            .border(2.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                }
            }
            items(games.size) { index ->
                val game = games[index]
                Row(
                    Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        game.date, modifier = Modifier
                            .weight(1f, true)
                            .height(48.dp)
                            .border(1.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                    Text(
                        game.player1Score.toString(), modifier = Modifier
                            .weight(1f, true)
                            .height(48.dp)
                            .border(1.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                    Text(
                        game.player2Score.toString(), modifier = Modifier
                            .weight(1f, true)
                            .height(48.dp)
                            .border(1.dp, MaterialTheme.colors.onBackground)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}
