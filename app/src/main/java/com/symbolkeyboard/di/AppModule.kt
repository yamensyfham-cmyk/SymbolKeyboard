package com.symbolkeyboard.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.symbolkeyboard.data.datastore.UserPreferencesStore
import com.symbolkeyboard.data.local.SymbolDatabase
import com.symbolkeyboard.data.repository.SymbolRepository
import com.symbolkeyboard.data.repository.SymbolRepositoryImpl
import com.symbolkeyboard.util.PowerSaver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SymbolDatabase {
        return Room.databaseBuilder(
            context,
            SymbolDatabase::class.java,
            "symbol_keyboard.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSymbolDao(database: SymbolDatabase) = database.symbolDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(database: SymbolDatabase) = database.favoriteDao()

    @Provides
    @Singleton
    fun provideRecentDao(database: SymbolDatabase) = database.recentDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferencesStore(dataStore: DataStore<Preferences>): UserPreferencesStore {
        return UserPreferencesStore(dataStore)
    }

    @Provides
    @Singleton
    fun provideSymbolRepository(impl: SymbolRepositoryImpl): SymbolRepository = impl

    @Provides
    @Singleton
    fun providePowerSaver(@ApplicationContext context: Context): PowerSaver {
        return PowerSaver(context)
    }
}
