package com.stephenwanjala.composemusic.di

import android.app.Application
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.stephenwanjala.composemusic.data.local.datasource.AudioSource
import com.stephenwanjala.composemusic.data.local.repositoryImpl.AudioRepositoryImpl
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@UnstableApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext application: Application): Context =
        application.applicationContext

    @Provides
    @Singleton
    fun provideAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    @Provides
    @Singleton
    fun provideExoplayer(
        @ApplicationContext application: Context,
        audioAttributes: AudioAttributes
    ): ExoPlayer = ExoPlayer.Builder(application)
        .build()
        .apply {
            setHandleAudioBecomingNoisy(true)
            setAudioAttributes(/* audioAttributes = */ audioAttributes,/* handleAudioFocus = */
                true
            )
        }

    @Singleton
    @Provides
    fun provideAudioSource(@ApplicationContext app: Context): AudioSource = AudioSource(app)

    @Provides
    @Singleton
    fun provideAudioRepository(audioSource: AudioSource): AudioRepository =
        AudioRepositoryImpl(audioSource)

}