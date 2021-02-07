package com.example.hungamaassign.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "recent_search")
data class RecentSearch (
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String
)