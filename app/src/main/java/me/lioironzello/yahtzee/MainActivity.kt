package me.lioironzello.yahtzee

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import me.lioironzello.yahtzee.ui.model.SettingsModel
import me.lioironzello.yahtzee.ui.screen.MainLayout
import me.lioironzello.yahtzee.ui.theme.Background
import me.lioironzello.yahtzee.ui.theme.Dice
import me.lioironzello.yahtzee.ui.theme.YahtzeeTheme
import java.util.*

@ExperimentalPagerApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        // Getting settings from SharedPreferences
        val defaultLanguage =
            if (resources.configuration.locales.get(0) == Locale.ITALIAN) "it"
            else "en"
        val language =
            sharedPreferences.getString("language", defaultLanguage) ?: defaultLanguage
        // Setting the application language
        resources.configuration.setLocale(Locale(language))
        val backgroundColor = Background.values()[sharedPreferences.getInt("background", 0)]
        val dice = Dice.values()[sharedPreferences.getInt("dice", 0)]
        val diceVelocity = DiceVelocity.values()[sharedPreferences.getInt("diceVelocity", 1)]

        super.onCreate(savedInstanceState)
        setContent {
            val darkTheme = sharedPreferences.getBoolean("darkTheme", isSystemInDarkTheme())
            val settings = rememberSaveable { SettingsModel() }
            settings.language = language
            settings.darkTheme = darkTheme
            settings.background = backgroundColor
            settings.dice = dice
            settings.diceVelocity = diceVelocity

            YahtzeeTheme(settings.darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainLayout(settings)
                }
            }
        }
    }
}

// TODO(add faster velocity, move to Dice model class)
enum class DiceVelocity { Medium, Fast }
