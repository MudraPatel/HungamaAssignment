package com.example.hungamaassign

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hungamaassign.dao.DetailsDao
import com.example.hungamaassign.dao.HomeMovieDao
import com.example.hungamaassign.model.CastModel
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import com.example.hungamaassign.model.SimilarvideoModel

@Database(entities = arrayOf(Movie::class, RecentSearch::class, CastModel::class, SimilarvideoModel::class), version = 1, exportSchema = false)
public abstract class AppDatabase : RoomDatabase() {

    abstract fun homeMovieDao() : HomeMovieDao
    abstract fun detailsDao() : DetailsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance =
                INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hungama_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}