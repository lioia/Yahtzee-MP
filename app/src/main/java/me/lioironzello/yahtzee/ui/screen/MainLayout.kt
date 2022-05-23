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
import androidx.compose.ui.unit.sp
import me.lioironzello.yahtzee.R

@Composable
fun MainLayout() {
    when (ScreenRouter.currentScreen) {
        Screens.Home -> Home()
        Screens.Settings -> TODO()
        Screens.PreviousGames -> TODO()
        Screens.Tutorial -> TODO()
        Screens.Play -> TODO()
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
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Outlined.Settings, contentDescription = "Settings")
            }
        }
        Spacer(modifier = Modifier.height(128.dp))
        Text(
            "Yahtzee",
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Cursive,
        )
        Spacer(modifier = Modifier.height(128.dp))
        Button(
            onClick = { /*TODO*/ }, modifier = Modifier
                .width(192.dp)
                .height(64.dp), elevation = ButtonDefaults.elevation(8.dp)
        ) {
            Text(stringResource(R.string.play), fontSize = 32.sp)
        }
        Spacer(modifier = Modifier.height(48.dp))
        OutlinedButton(onClick = { /*TODO*/ }) {
            Text(stringResource(R.string.previous_games))
        }
    }
}
