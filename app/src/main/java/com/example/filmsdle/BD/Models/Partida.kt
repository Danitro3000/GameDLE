package com.example.filmsdle.BD.Models

import android.graphics.Color
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.sql.Time
import java.time.Duration

@Parcelize
@Entity
data class Partida(
    @PrimaryKey
    var partidaId: Int? = null,
    var peliculaId: Int,
    var usuario1Id: Int,
    var usuario2Id: Int,
    var tiempoUsuario1: String,
    var tiempoUsuario2: String
):Parcelable