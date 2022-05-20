package com.example.instatram.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class PhotoDatabase: RoomDatabase() {
    abstract fun PhotoDao(): PhotoDao
    companion object {
        @Volatile
        private var INSTANCE: PhotoDatabase? = null
        fun getDatabase(context: Context): PhotoDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        PhotoDatabase::class.java,
                        "monsters.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}