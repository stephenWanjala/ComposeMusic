package com.stephenwanjala.composemusic.data.local.datasource

import android.media.MediaDescription
import android.media.browse.MediaBrowser
import android.media.browse.MediaBrowser.MediaItem.FLAG_PLAYABLE
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.stephenwanjala.composemusic.domain.model.ModelAudio
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import javax.inject.Inject

@UnstableApi
class ComposeMusicMediaSource @Inject constructor(
    private val repository: AudioRepository,
) {
    private val onReadyListeners: MutableList<OnReadyListener> = mutableListOf()
    private var audioMetadata: List<MediaMetadata> = emptyList()
    private val isReady: Boolean
        get() = state == MediaSourceSte.STATE_INITIALIZED
    private var state: MediaSourceSte = MediaSourceSte.STATE_CREATED
        set(value) =
            if (value == MediaSourceSte.STATE_CREATED || value == MediaSourceSte.STATE_ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                }
                onReadyListeners.forEach { listener ->
                    listener.invoke(isReady)
                }
            } else {
                field = value
            }

    fun whenReady(listener: OnReadyListener): Boolean = when (state) {
        MediaSourceSte.STATE_CREATED,
        MediaSourceSte.STATE_INITIALIZING,
        -> {
            onReadyListeners += listener
            false
        }

        MediaSourceSte.STATE_INITIALIZED,
        MediaSourceSte.STATE_ERROR,
        -> {
            listener.invoke(isReady)
            true
        }
    }


    suspend fun loadMediaFromRepository() {
        state = MediaSourceSte.STATE_INITIALIZING
        val data = repository.audioDataList()
        audioMetadata = data.map { modelAudio: ModelAudio ->
            MediaMetadata.Builder()
                .apply {
                    setArtist(modelAudio.artist)
                    setTitle(modelAudio.tittle)
                    setDisplayTitle(modelAudio.displayName)
                    setAlbumArtist(modelAudio.albumArtist)
                    setAlbumTitle(modelAudio.album)
                    setArtworkUri(modelAudio.uri)
                }
                .build()


        }
        state = MediaSourceSte.STATE_INITIALIZED
    }

    //    media metadata items playable
    fun asMediaSource(datasourceFactoryFactory: DataSource.Factory): ConcatenatingMediaSource {
        val mediaItem = MediaItem.fromUri(android.media.MediaMetadata.METADATA_KEY_MEDIA_URI)
        val concatenatingMediaSource = ConcatenatingMediaSource()
        val mediaSource = ProgressiveMediaSource.Factory(datasourceFactoryFactory)
            .createMediaSource(mediaItem)
        concatenatingMediaSource.addMediaSource(mediaSource)
        return concatenatingMediaSource
    }

    //    displayable
    fun asMediaDescItem() = audioMetadata.map { mediaMetadata: MediaMetadata ->
        val description = MediaDescription.Builder()
            .apply {
                setTitle(mediaMetadata.title)
                setMediaUri(mediaMetadata.artworkUri)
                setDescription(mediaMetadata.description)
                setSubtitle(mediaMetadata.subtitle)
            }
            .build()

        MediaBrowser.MediaItem(description, FLAG_PLAYABLE)

    }.toMutableList()

    fun refreshState() {
        onReadyListeners.clear()
        state = MediaSourceSte.STATE_CREATED
    }

}

enum class MediaSourceSte {
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR,
}


typealias OnReadyListener = (Boolean) -> Unit