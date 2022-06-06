package me.lioironzello.yahtzee.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Game(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: String,
    val player1Score: Int,
    val player2Score: Int?
)
