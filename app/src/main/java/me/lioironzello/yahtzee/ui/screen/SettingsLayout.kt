package me.lioironzello.yahtzee.ui.screen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.ui.model.SettingsModel
import me.lioironzello.yahtzee.ui.theme.Background
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun SettingsLayout(settingsModel: SettingsModel) {
    val context = LocalContext.current

    val languages = mapOf("en" to "English", "it" to "Italiano")
    var languageExpanded by remember { mutableStateOf(false) }

    val language = rememberSaveable { mutableStateOf(settingsModel.language) }
    val darkTheme = rememberSaveable { mutableStateOf(settingsModel.darkTheme) }
    val background = rememberSaveable { mutableStateOf(settingsModel.background) }
    val dice = rememberSaveable { mutableStateOf(settingsModel.dice) }
    val diceVelocity = rememberSaveable { mutableStateOf(settingsModel.diceVelocity) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            stringResource(R.string.settings),
            style = MaterialTheme.typography.h3,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
        // Language
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.body1)
            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = !languageExpanded }) {
                OutlinedTextField(
                    modifier = Modifier.width(192.dp),
                    value = languages[language.value] ?: "English",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }) {
                    languages.forEach { (code, lang) ->
                        DropdownMenuItem(onClick = {
                            language.value = code
                            languageExpanded = false
                        }) { Text(lang) }
                    }
                }
            }
        }
        // Dark Theme
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.dark_theme), style = MaterialTheme.typography.body1)
            Switch(
                checked = darkTheme.value,
                onCheckedChange = { darkTheme.value = it }
            )
        }
        // Background Color
        Text(stringResource(R.string.background_style), style = MaterialTheme.typography.body1)
        HorizontalPager(
            count = Background.values().size,
            modifier = Modifier.padding(8.dp)
        ) { page ->
            Card(
                Modifier
                    .graphicsLayer {
                        // Calculate the absolute offset for the current page from the
                        // scroll position. We use the absolute value which allows us to mirror
                        // any effects for both directions
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        // We animate the scaleX + scaleY, between 85% and 100%
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        // We animate the alpha, between 50% and 100%
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            ) {
                val bg = Background.values()[page]
                val border = if (bg == background.value) 6.dp else 0.dp
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(
                                border,
                                Color(255, 244, 56)
                            )
                        )
                        .clickable { background.value = bg }
                ) {
                    when (bg) {
                        Background.White -> Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green"
                        )
                        Background.Black -> Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green"
                        )
                        Background.Blue -> Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green"
                        )
                        Background.Green -> Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green"
                        )
                        Background.Wood -> Image(
                            painter = painterResource(R.drawable.green),
                            contentDescription = "Green"
                        )
                    }
                }
            }
        }
        // Dice Color
        Text(stringResource(R.string.background_style), style = MaterialTheme.typography.body1)
        // TODO(dice color)
//        HorizontalPager(count = Background.values().size, modifier = Modifier.padding(8.dp)) { page ->
//            Card(
//                Modifier
//                    .graphicsLayer {
//                        // Calculate the absolute offset for the current page from the
//                        // scroll position. We use the absolute value which allows us to mirror
//                        // any effects for both directions
//                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue
//
//                        // We animate the scaleX + scaleY, between 85% and 100%
//                        lerp(
//                            start = 0.85f,
//                            stop = 1f,
//                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                        ).also { scale ->
//                            scaleX = scale
//                            scaleY = scale
//                        }
//
//                        // We animate the alpha, between 50% and 100%
//                        alpha = lerp(
//                            start = 0.5f,
//                            stop = 1f,
//                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
//                        )
//                    }
//            ) {
//                val bg = Background.values()[page]
//                val border = if (bg == background.value) 6.dp else 0.dp
//                Box(
//                    modifier = Modifier.border(
//                        BorderStroke(
//                            border,
//                            Color(255, 244, 56)
//                        )
//                    ).clickable { background.value = bg }
//                ) {
//                    when (bg) {
//                        Background.White -> Image(
//                            painter = painterResource(R.drawable.green),
//                            contentDescription = "Green"
//                        )
//                        Background.Black -> Image(
//                            painter = painterResource(R.drawable.green),
//                            contentDescription = "Green"
//                        )
//                        Background.Blue -> Image(
//                            painter = painterResource(R.drawable.green),
//                            contentDescription = "Green"
//                        )
//                        Background.Green -> Image(
//                            painter = painterResource(R.drawable.green),
//                            contentDescription = "Green"
//                        )
//                        Background.Wood -> Image(
//                            painter = painterResource(R.drawable.green),
//                            contentDescription = "Green"
//                        )
//                    }
//                }
//            }
//        }
        // Dice Velocity
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.roll_velocity), style = MaterialTheme.typography.body1)
            Row(verticalAlignment = Alignment.CenterVertically) {
                DiceVelocity.values().forEach {
                    RadioButton(
                        selected = diceVelocity.value == it,
                        onClick = { diceVelocity.value = it })
                    Text(it.name)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.End)
                .width(128.dp)
                .height(48.dp),
            onClick = {
                settingsModel.language = language.value
                settingsModel.darkTheme = darkTheme.value
                settingsModel.background = background.value
                settingsModel.dice = dice.value
                settingsModel.diceVelocity = diceVelocity.value
                val sharedPreferences =
                    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("language", settingsModel.language)
                editor.putBoolean("darkTheme", settingsModel.darkTheme)
                editor.putInt("background", settingsModel.background.ordinal)
                editor.putInt("dice", settingsModel.dice.ordinal)
                editor.putInt("diceVelocity", settingsModel.diceVelocity.ordinal)
                editor.apply()
                ScreenRouter.navigateTo(Screens.Home)
            }) {
            Text(stringResource(R.string.save), style = MaterialTheme.typography.body1)
        }
        BackHandler {
            ScreenRouter.navigateTo(Screens.Home)
        }
    }
}
