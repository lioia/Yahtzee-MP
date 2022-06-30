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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import me.lioironzello.yahtzee.model.DiceColor
import me.lioironzello.yahtzee.model.DiceVelocity
import me.lioironzello.yahtzee.model.SettingsModel
import me.lioironzello.yahtzee.ui.screen.MainLayout
import me.lioironzello.yahtzee.ui.theme.YahtzeeTheme

@ExperimentalAnimationApi
@ExperimentalPagerApi
@ExperimentalMaterialApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Getting OpenGL ES Version (used for 3d rendering the dice model)
        // From https://android.googlesource.com/platform/cts/+/73e3f96/tests/tests/graphics/src/android/opengl/cts/OpenGlEsVersionTest.java#151
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        val glVersion =
            if (configurationInfo.reqGlEsVersion != ConfigurationInfo.GL_ES_VERSION_UNDEFINED) {
                (configurationInfo.reqGlEsVersion and 0xffff0000.toInt()) shr 16
            } else 1

        super.onCreate(savedInstanceState)
        setContent {
            // Getting settings from SharedPreferences
            val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
            val diceColor = DiceColor.values()[sharedPreferences.getInt("dice", 0)]
            var diceVelocity = DiceVelocity.values()[sharedPreferences.getInt("diceVelocity", 0)]
            val soundEnabled = sharedPreferences.getBoolean("soundEnabled", true)
            if (glVersion != 3 && diceVelocity == DiceVelocity.Slow) diceVelocity =
                DiceVelocity.Medium
            val darkTheme = sharedPreferences.getBoolean("darkTheme", isSystemInDarkTheme())
            // Creating the SettingsModel
            val settings = rememberSaveable { mutableStateOf(SettingsModel()) }
            settings.value.darkTheme = darkTheme
            settings.value.diceColor = diceColor
            settings.value.diceVelocity = diceVelocity
            settings.value.soundEnabled = soundEnabled
            settings.value.glVersion = glVersion

            YahtzeeTheme(settings.value.darkTheme) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainLayout(settings.value)
                }
            }
        }
    }
}
