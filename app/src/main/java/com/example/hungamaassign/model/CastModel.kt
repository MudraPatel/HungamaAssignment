package com.example.hungamaassign.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cast_table")
data class CastModel (

        @PrimaryKey()
        val id: Int,
        val castId: String,
        val name: String,
        val profileUrl: String,
        val knownDepartment: String,
        val gender: Int,
        val popularity: Double,
        val type:String
)