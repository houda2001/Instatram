package com.example.instatram.data

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import com.example.instatram.WEB_SERVICE_URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class StationRepository(val app: Application) {
    val stationData = MutableLiveData<List<Station>>()

    // 3. Appeler la méthode callWebService() en background en mode non-bloquant (via Coroutines)
    init {
        CoroutineScope(Dispatchers.IO).launch {
            callWebService()
        }
    }

    // 2. Méthode permettante de consommer le service web (via retrofit et Moshi)
    @WorkerThread
    suspend fun callWebService() {
        if (networkAvailable()) {
            val retrofit = Retrofit.Builder()
                .baseUrl(WEB_SERVICE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
            val service = retrofit.create(StationService::class.java)
            val serviceData = service.getStationData().body() ?: emptyList()
            stationData.postValue(serviceData)
        }
    }

    // 1. Vérifier si la connexion Internet est en marche ou pas
    @Suppress("DEPRECATION")
    private fun networkAvailable(): Boolean {
        val connectivityManager = app.getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false
    }
}