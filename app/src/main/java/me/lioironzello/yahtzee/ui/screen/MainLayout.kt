package me.lioironzello.yahtzee.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.ui.model.SettingsModel

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun MainLayout(settingsModel: SettingsModel) {
    var numberOfPlayers by remember { mutableStateOf(1) }

    when (ScreenRouter.currentScreen) {
        Screens.Home -> Home { num -> numberOfPlayers = num }
        Screens.Settings -> SettingsLayout(settingsModel)
        Screens.PreviousGames -> TODO()
        Screens.Tutorial -> TODO()
        Screens.Play -> PlayLayout(settingsModel, numberOfPlayers)
    }
}

@Composable
fun Home(setNumberOfPlayers: (players: Int) -> Unit) {
    var playDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Outlined.HelpOutline, contentDescription = "Tutorial")
            }
            IconButton(onClick = { ScreenRouter.navigateTo(Screens.Settings) }) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
        Spacer(modifier = Modifier.height(128.dp))
        Text(
            stringResource(R.string.yahtzee),
            style = MaterialTheme.typography.h1,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(128.dp))
        Button(
            onClick = { playDialog = true }, modifier = Modifier
                .width(192.dp)
                .height(64.dp), elevation = ButtonDefaults.elevation(8.dp)
        ) {
            Text(stringResource(R.string.play), style = MaterialTheme.typography.h5)
        }
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.previous_games), style = MaterialTheme.typography.button)
        }

        if (playDialog) {
            AlertDialog(onDismissRequest = { playDialog = false },
                title = { Text(stringResource(R.string.select_num_players)) },
                text = {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            setNumberOfPlayers(1)
                            playDialog = false
                            ScreenRouter.navigateTo(Screens.Play)
                        }) {
                            Text("1")
                        }
                        Button(onClick = {
                            setNumberOfPlayers(2)
                            playDialog = false
                            ScreenRouter.navigateTo(Screens.Play)
                        }) {
                            Text("2")
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    Button(onClick = { playDialog = false }) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }
    }
}
