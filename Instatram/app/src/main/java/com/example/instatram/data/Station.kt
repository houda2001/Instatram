package com.example.instatram.data

data class Station(
    val id: Int,
    val line: String,
    val name: String,
    val type: String,
    val zone: String,
    val connections: String,
    val lat: Double,
    val lon: Double
)
