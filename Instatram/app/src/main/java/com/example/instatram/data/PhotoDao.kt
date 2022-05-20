package com.example.instatram.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface PhotoDao {
    @Query("SELECT * from photos WHERE stationId = :stationId")
    fun getPhotos(stationId: Int): List<Photo>

    @Query("SELECT * from photos WHERE id = :photoId")
    fun getPhoto(photoId: Int): Photo

    @Insert
    fun insertPhoto(photo: Photo)

    @Query("DELETE from photos WHERE id = :photoId")
    fun deletePhoto(photoId: Int)
}