package com.example.filmsdle.BD.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Invitation (
    @PrimaryKey
    val invitationId: Int,
    val senderId: Int,
    val receiverId: Int,
    val message: String,
    val invitationSent: Boolean,
    val invitationReceived: Boolean
)
