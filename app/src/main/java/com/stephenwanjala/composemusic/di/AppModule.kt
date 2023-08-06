package com.stephenwanjala.composemusic.di

import android.app.Application
import com.stephenwanjala.composemusic.data.local.datasource.AudioSource
import com.stephenwanjala.composemusic.data.local.repositoryImpl.AudioRepositoryImpl
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAudioSource(app: Application): AudioSource = AudioSource(app)

    @Provides
    @Singleton
    fun provideAudioRepository(audioSource: AudioSource): AudioRepository =
        AudioRepositoryImpl(audioSource)
}