package com.example.instatram.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.instatram.data.StationRepository

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val dataRepo = StationRepository(app)
    var stationData = dataRepo.stationData
    var wasClicked = false
}