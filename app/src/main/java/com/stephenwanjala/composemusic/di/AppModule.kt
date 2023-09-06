package com.stephenwanjala.composemusic.di

import android.app.Application
import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.NoOpCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import com.stephenwanjala.composemusic.data.local.datasource.AudioSource
import com.stephenwanjala.composemusic.data.local.repositoryImpl.AudioRepositoryImpl
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@UnstableApi @Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext application: Application) =
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

    @Provides
    @Singleton
    fun provideDatasourceFactory(@ApplicationContext app: Context): DataSource.Factory =
        DefaultDataSource.Factory(/* context = */ app)

    @Provides
    @Singleton
    fun provideCacheDatasourceFactory(
        @ApplicationContext app: Context,
        factory: DataSource.Factory
    ): CacheDataSource.Factory {
        val cacheDir = File(app.cacheDir, "media")
        val databaseProvider = StandaloneDatabaseProvider(app)
        val cache = SimpleCache(/* cacheDir = */
            cacheDir, /* evictor = */
            NoOpCacheEvictor(), /* databaseProvider = */
            databaseProvider
        )
        return CacheDataSource.Factory().apply {
            setCache(cache)
            setUpstreamDataSourceFactory { factory.createDataSource() }
        }
    }

    @Singleton
    @Provides
    fun provideAudioSource(@ApplicationContext app: Context): AudioSource = AudioSource(app)

    @Provides
    @Singleton
    fun provideAudioRepository(audioSource: AudioSource): AudioRepository =
        AudioRepositoryImpl(audioSource)

}