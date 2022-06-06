package me.lioironzello.yahtzee.ui.screen

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.model.DiceColor
import me.lioironzello.yahtzee.model.DiceVelocity
import me.lioironzello.yahtzee.model.SettingsModel
import kotlin.math.absoluteValue

@ExperimentalPagerApi
@ExperimentalMaterialApi
@Composable
fun SettingsLayout(settingsModel: SettingsModel) {
    val context = LocalContext.current

    val languages = mapOf("en" to "English", "it" to "Italiano")
    var languageExpanded by remember { mutableStateOf(false) }
    var velocityExpanded by remember { mutableStateOf(false) }

    val language = rememberSaveable { mutableStateOf(settingsModel.language) }
    val darkTheme = rememberSaveable { mutableStateOf(settingsModel.darkTheme) }
    val dice = rememberSaveable { mutableStateOf(settingsModel.diceColor) }
    val diceVelocity = rememberSaveable { mutableStateOf(settingsModel.diceVelocity) }

    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { ScreenRouter.navigateTo(Screens.Home) }) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp
        )
        Column(Modifier.padding(16.dp)) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(R.string.dark_theme), style = MaterialTheme.typography.body1)
                Switch(
                    checked = darkTheme.value,
                    onCheckedChange = { darkTheme.value = it }
                )
            }
            // Dice Color
            Text(stringResource(R.string.dice_color), style = MaterialTheme.typography.body1)
            HorizontalPager(
                count = DiceColor.values().size,
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
                    val diceColor = DiceColor.values()[page]
//                val border = if (diceColor == dice.value) 6.dp else 0.dp
                    val borderColor =
                        if (diceColor == dice.value) Color(255, 244, 56) else Color.Black
                    Box(
                        modifier = Modifier
                            .border(BorderStroke(6.dp, borderColor))
                            .size(80.dp)
                            .background(diceColor.color)
                            .clickable { dice.value = diceColor }
                    )
                }
            }
            // Dice Velocity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.roll_velocity), style = MaterialTheme.typography.body1)
                ExposedDropdownMenuBox(
                    expanded = velocityExpanded,
                    onExpandedChange = { velocityExpanded = !velocityExpanded }) {
                    OutlinedTextField(
                        modifier = Modifier.width(192.dp),
                        value = diceVelocity.value.name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = velocityExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = velocityExpanded,
                        onDismissRequest = { velocityExpanded = false }) {
                        if (settingsModel.glVersion == 3)
                            DropdownMenuItem(onClick = {
                                diceVelocity.value = DiceVelocity.Slow
                                velocityExpanded = false
                            }) { Text(stringResource(R.string.slow)) }
                        DropdownMenuItem(onClick = {
                            diceVelocity.value = DiceVelocity.Medium
                            velocityExpanded = false
                        }) { Text(stringResource(R.string.medium)) }
                        DropdownMenuItem(onClick = {
                            diceVelocity.value = DiceVelocity.Fast
                            velocityExpanded = false
                        }) { Text(stringResource(R.string.fast)) }
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
                    settingsModel.diceColor = dice.value
                    settingsModel.diceVelocity = diceVelocity.value
                    val sharedPreferences =
                        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("language", settingsModel.language)
                    editor.putBoolean("darkTheme", settingsModel.darkTheme)
                    editor.putInt("dice", settingsModel.diceColor.ordinal)
                    editor.putInt("diceVelocity", settingsModel.diceVelocity.ordinal)
                    editor.apply()
                    ScreenRouter.navigateTo(Screens.Home)
                }) {
                Text(stringResource(R.string.save), style = MaterialTheme.typography.body1)
            }
        }
        BackHandler {
            ScreenRouter.navigateTo(Screens.Home)
        }
    }
}
