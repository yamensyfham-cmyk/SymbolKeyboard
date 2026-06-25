package com.symbolkeyboard.di

import android.content.Context
import com.symbolkeyboard.util.SymbolParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeyboardModule {

    @Provides
    @Singleton
    fun provideSymbolParser(@ApplicationContext context: Context): SymbolParser {
        return SymbolParser(context)
    }
}
