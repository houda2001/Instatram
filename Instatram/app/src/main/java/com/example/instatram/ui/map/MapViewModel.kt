package com.example.instatram.ui.map

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.instatram.data.StationRepository

class MapViewModel(app: Application) : AndroidViewModel(app) {
    private val dataRepo = StationRepository(app)
    var stationData = dataRepo.stationData
}