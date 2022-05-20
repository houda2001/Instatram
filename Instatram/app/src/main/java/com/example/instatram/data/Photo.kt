package com.example.instatram.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val date: String,
    val stationId: Int,
    val imageUri: String
)
