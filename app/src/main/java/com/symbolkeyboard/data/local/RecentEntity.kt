package com.symbolkeyboard.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recents")
data class RecentEntity(
    @PrimaryKey
    val symbolUnicode: String,
    val usedAt: Long = System.currentTimeMillis()
)
