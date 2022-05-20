package com.example.instatram.displayImage

import android.app.Application
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import com.example.instatram.data.Photo
import com.example.instatram.data.PhotoDatabase

class DisplayImageViewModel(app: Application) : AndroidViewModel(app) {

    var wasClicked = false

    private val photoDao = PhotoDatabase.getDatabase(app)
        .PhotoDao()

    @WorkerThread
    fun insertPhoto(photo: Photo) {
        photoDao.insertPhoto(photo)
    }

    @WorkerThread
    fun deletePhoto(photoId: Int) {
        photoDao.deletePhoto(photoId)
    }

    @WorkerThread
    fun getPhoto(photoId: Int): Photo {
        return photoDao.getPhoto(photoId)
    }

    @WorkerThread
    fun getPhotos(stationId: Int): List<Photo> {
        return photoDao.getPhotos(stationId)
    }
}