package me.lioironzello.yahtzee.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import me.lioironzello.yahtzee.model.Game

@Database(entities = [Game::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    companion object {
        private var db: GameDatabase? = null
        fun getInstance(context: Context): GameDatabase {
            if (db == null) db = Room.databaseBuilder(
                context.applicationContext,
                GameDatabase::class.java,
                "previousgames.db"
            ).build()
            return db as GameDatabase
        }
    }

    abstract fun gameDao(): GameDao
}
