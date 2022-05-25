package me.lioironzello.yahtzee.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.lioironzello.yahtzee.DiceVelocity
import me.lioironzello.yahtzee.R
import me.lioironzello.yahtzee.ui.model.SettingsModel
import java.util.*

// TODO(not working)
@ExperimentalMaterialApi
@Composable
fun SettingsLayout(settingsModel: MutableState<SettingsModel>) {
    val context = LocalContext.current

    val languages = mapOf("en" to "English", "it" to "Italiano")
    var languageExpanded by remember { mutableStateOf(false) }

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
                    value = languages[settingsModel.value.language] ?: "English",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = languageExpanded) },
                )
                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }) {
                    languages.forEach { (code, language) ->
                        DropdownMenuItem(onClick = {
                            settingsModel.value.language = code
                            languageExpanded = false
                            context.resources.configuration.setLocale(Locale(code))
                            // TODO(save language in SharedPreferences)
                        }) { Text(language) }
                    }
                }
            }
        }
        // Dark Theme
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.dark_theme), style = MaterialTheme.typography.body1)
            println("1")
            Switch(
                checked = settingsModel.value.darkTheme,
                onCheckedChange = {
                    settingsModel.value.darkTheme = it
                    println(settingsModel.value.darkTheme.toString())
                })
        }
        // Colors
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
                        selected = settingsModel.value.diceVelocity == it,
                        onClick = { settingsModel.value.diceVelocity = it })
                    Text(it.name)
                }
            }
        }
        BackHandler {
            ScreenRouter.navigateTo(Screens.Home)
        }
    }
}
