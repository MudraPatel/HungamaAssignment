package com.example.hungamaassign.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hungamaassign.model.CastModel
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch
import com.example.hungamaassign.model.SimilarvideoModel

@Dao
interface   DetailsDao {

    @Query("SELECT * FROM cast_table WHERE type = 'cast' ORDER BY id ASC")
    fun getAllCast(): LiveData<List<CastModel>>

    @Query("SELECT * FROM cast_table WHERE type = 'crew' ORDER BY id DESC")
    fun getAllCrew(): LiveData<List<CastModel>>

    @Query("SELECT * FROM similar_video ORDER BY id DESC")
    fun getAllSimilar(): LiveData<List<SimilarvideoModel>>



    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertAll(cast: List<CastModel>)

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertAllSimilarList(similar: List<SimilarvideoModel>)

}