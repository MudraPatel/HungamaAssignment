package com.example.hungamaassign.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "home_movie")
class Movie(

      @PrimaryKey
      val id: Int,
      val title : String,
      val overview : String,
      val poster_path : String,
      val release_date : String,
      val video : Boolean,
      val popularity : Double,
      val vote_average : Int,
      val vote_count : Int,
      val original_language : String

)