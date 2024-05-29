package com.example.filmsdle.BD.Models

import android.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AllThings(
    val actor: List<Actor>,
    val company: List<Company>,
    val country: List<Country>,
    val genre: List<Genre>,
    val language: List<Language>,
    val movie: Movie,
    @PrimaryKey
    val movieId: Int,
    val papel: List<Papel>
)