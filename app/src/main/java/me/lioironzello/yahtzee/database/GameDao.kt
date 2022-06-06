package me.lioironzello.yahtzee.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import me.lioironzello.yahtzee.model.Game

@Dao
interface GameDao {
    @Insert
    fun insert(game: Game)

    @Query("SELECT * FROM Game")
    fun loadAll(): LiveData<List<Game>>
}
