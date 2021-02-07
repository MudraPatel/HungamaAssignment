package com.example.hungamaassign.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hungamaassign.model.Movie
import com.example.hungamaassign.model.RecentSearch

@Dao
interface   HomeMovieDao {

//    @get:Query("SELECT * FROM home_movie ORDER BY id DESC")
//    val allPrograms: LiveData<List<Movie>>

    @Query("SELECT * FROM home_movie ORDER BY id DESC")
    fun getAllHomePrograms(): LiveData<List<Movie>>

    @Query("SELECT * FROM home_movie WHERE title LIKE  '%' || :searchquery || '%'")
    fun searchForProgram(searchquery: String): LiveData<List<Movie>>

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insert(program: Movie)

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertAll(program: List<Movie>)


    @get:Query("SELECT * FROM recent_search ORDER BY id DESC")
    val allRecentSearch: LiveData<List<RecentSearch>>

    @Query("SELECT count(*) FROM recent_search")
   suspend fun recentListCount(): Int

    @get:Query("SELECT * FROM recent_search ORDER BY id ASC limit 1")
    val recentSearchAsc: LiveData<RecentSearch>

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertRecent(search: RecentSearch)

        @Query("DELETE FROM recent_search WHERE id = (SELECT id FROM recent_search ORDER BY id ASC limit 1)")
    suspend  fun deleteSearch()


    /* @Insert(onConflict = OnConflictStrategy.IGNORE)
     suspend fun insertData(program: HomeProgram)*/

    @Update
    fun update(program: Movie)

    @Delete
    suspend fun delete(program: Movie)

    @Query("DELETE FROM home_movie")
    fun deleteAllPrograms()
}