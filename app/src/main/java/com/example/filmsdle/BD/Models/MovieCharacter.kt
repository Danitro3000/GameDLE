package com.example.filmsdle.BD.Models

import androidx.room.Entity

@Entity
data class MovieCharacter (
    val movieTitle: String,
    val characterName: String
)