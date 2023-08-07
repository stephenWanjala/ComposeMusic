package com.stephenwanjala.composemusic.di

import android.app.Application
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import java.io.File

@UnstableApi
@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @Provides
    @ServiceScoped
    fun provideAudioAttributes(): AudioAttributes =
        AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()

    @Provides
    @ServiceScoped
    fun provideExoplayer(
        @ApplicationContext application: Application,
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
    @ServiceScoped
    fun provideDatasourceFactory(@ApplicationContext app: Application): DataSource.Factory =
        DefaultDataSource.Factory(/* context = */ app)

    @Provides
    @ServiceScoped
    fun provideCacheDatasourceFactory(
        @ApplicationContext app: Application,
        factory: DefaultDataSource.Factory
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

}