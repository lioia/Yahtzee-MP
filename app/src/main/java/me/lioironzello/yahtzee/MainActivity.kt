package me.lioironzello.yahtzee

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ConfigurationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.ExperimentalPagerApi
import me.lioironzello.yahtzee.model.DiceColor
import me.lioironzello.yahtzee.model.DiceVelocity
import me.lioironzello.yahtzee.model.SettingsModel
import me.lioironzello.yahtzee.ui.screen.MainLayout
import me.lioironzello.yahtzee.ui.theme.YahtzeeTheme
import java.util.*

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Getting OpenGL ES Version
        // From https://android.googlesource.com/platform/cts/+/73e3f96/tests/tests/graphics/src/android/opengl/cts/OpenGlEsVersionTest.java
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val glVersion =
            if (configurationInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
                (configurationInfo.reqGlEsVersion and 0xffff0000.toInt()) shr 16
            } else 1

        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        // Getting settings from SharedPreferences
        val defaultLanguage =
            if (resources.configuration.locales.get(0) == Locale.ITALIAN) "it"
            else "en"
        val language =
            sharedPreferences.getString("language", defaultLanguage) ?: defaultLanguage
        // Setting the application language
        val locale = Locale(language)
        val config = resources.configuration
        Locale.setDefault(locale)
        config.setLocale(locale)
//        resources.configuration.setLocale(locale)
        createConfigurationContext(config)
//        resources.updateConfiguration(config, resources.displayMetrics)

        val diceColor = DiceColor.values()[sharedPreferences.getInt("dice", 0)]
        var diceVelocity = DiceVelocity.values()[sharedPreferences.getInt("diceVelocity", 0)]
        if (glVersion != 3 && diceVelocity == DiceVelocity.Slow) diceVelocity = DiceVelocity.Medium

        super.onCreate(savedInstanceState)
        setContent {

            val darkTheme = sharedPreferences.getBoolean("darkTheme", isSystemInDarkTheme())
            val settings = rememberSaveable { SettingsModel() }
            settings.language = language
            settings.darkTheme = darkTheme
            settings.diceColor = diceColor
            settings.diceVelocity = diceVelocity
            settings.glVersion = glVersion

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
