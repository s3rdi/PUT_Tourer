package com.example.tourer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val type: String,
    val image1: String = "",
    val image2: String = "",
    val image3: String = ""
)
