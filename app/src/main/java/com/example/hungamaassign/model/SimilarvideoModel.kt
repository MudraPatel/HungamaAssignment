package com.example.hungamaassign.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "similar_video")
data class SimilarvideoModel (
    @PrimaryKey()
    val id: Int,
    val title: String,
    val posterPath: String,
    val overview : String,
    val release_date : String,
    val video : Boolean,
    val popularity: Double,
)