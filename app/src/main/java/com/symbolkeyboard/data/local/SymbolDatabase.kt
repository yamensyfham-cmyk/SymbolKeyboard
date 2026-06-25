package com.symbolkeyboard.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.symbolkeyboard.data.model.Symbol

@Database(
    entities = [Symbol::class, FavoriteEntity::class, RecentEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SymbolDatabase : RoomDatabase() {
    abstract fun symbolDao(): SymbolDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao
}
