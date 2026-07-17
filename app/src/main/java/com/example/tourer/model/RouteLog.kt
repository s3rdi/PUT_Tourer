package com.example.tourer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routeLogs")
data class RouteLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routeId: Int,
    val timeInSeconds: Int,
    val dateInMillis: Long
)
