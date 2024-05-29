package com.example.filmsdle.BD.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ActorMoviesDTO(
    @PrimaryKey
    val actorId: Int,
    val actorName: String,
    val moviesAndCharacters: List<MovieCharacter>
)



