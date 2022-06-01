package me.lioironzello.yahtzee.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
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
    when (ScreenRouter.currentScreen) {
        Screens.Home -> Home()
        Screens.Settings -> SettingsLayout(settingsModel)
        Screens.PreviousGames -> TODO()
        Screens.Tutorial -> TODO()
        Screens.Play -> PlayLayout(settingsModel)
    }
}

@Composable
fun Home() {
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
            onClick = { ScreenRouter.navigateTo(Screens.Play) }, modifier = Modifier
                .width(192.dp)
                .height(64.dp), elevation = ButtonDefaults.elevation(8.dp)
        ) {
            Text(stringResource(R.string.play), style = MaterialTheme.typography.h5)
        }
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.previous_games), style = MaterialTheme.typography.button)
        }
    }
}
