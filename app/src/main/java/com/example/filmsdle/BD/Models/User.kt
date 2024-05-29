package com.example.filmsdle.BD.Models

import androidx.room.PrimaryKey

data class User (
    @PrimaryKey
    val id: Int,
    val nombre: String,
    val alias: String,
    val email: String,
    val password: String,
    val puntos: Int,
    val posicionRanking: Int
)