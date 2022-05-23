package me.lioironzello.yahtzee

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import me.lioironzello.yahtzee.ui.model.SettingsModel
import me.lioironzello.yahtzee.ui.screen.MainLayout
import me.lioironzello.yahtzee.ui.theme.Background
import me.lioironzello.yahtzee.ui.theme.Dice
import me.lioironzello.yahtzee.ui.theme.TypeSize
import me.lioironzello.yahtzee.ui.theme.YahtzeeTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        setContent {
            // Getting settings from SharedPreferences
            val defaultLanguage =
                if (resources.configuration.locales.get(0) == Locale.ITALIAN) "it"
                else "en"
            val language =
                sharedPreferences.getString("language", defaultLanguage) ?: defaultLanguage
            // Setting the application language
            resources.configuration.setLocale(Locale(language))
            val darkTheme = sharedPreferences.getBoolean("darkTheme", isSystemInDarkTheme())
            val textSize = when (sharedPreferences.getString("textSize", "Medium")) {
                "Small" -> TypeSize.Small
                "Large" -> TypeSize.Large
                else -> TypeSize.Medium
            }
            val backgroundColor = Background.values()[sharedPreferences.getInt("background", 0)]
            val dice = Dice.values()[sharedPreferences.getInt("dice", 0)]
            val diceVelocity = DiceVelocity.values()[sharedPreferences.getInt("diceVelocity", 1)]

            val settings by remember {
                mutableStateOf(
                    SettingsModel(
                        darkTheme,
                        textSize,
                        backgroundColor,
                        dice,
                        diceVelocity
                    )
                )
            }

            YahtzeeTheme(settings.darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainLayout()
                }
            }
        }
    }
}

// TODO(add faster velocity, move to Dice model class)
enum class DiceVelocity { Medium, Fast }
