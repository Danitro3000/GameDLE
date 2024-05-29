package com.example.filmsdle.BD.Models

data class Movie(
    val budget: Int,
    val homepage: String,
    val movie_id: Int,
    val movie_status: String,
    val overview: String,
    val popularity: Double,
    val release_Date: String,
    val revenue: Int,
    val runtime: Int,
    val tagline: String,
    val title: String,
    val vote_average: Double,
    val vote_count: Int
)