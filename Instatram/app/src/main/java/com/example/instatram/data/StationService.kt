package com.example.instatram.data

import retrofit2.Response
import retrofit2.http.GET

interface StationService {
    @GET("/b/6090a297d64cd16802a8e804")
    suspend fun getStationData(): Response<List<Station>>
}