package com.stephenwanjala.composemusic.data

import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ComposeMusicService : MediaSessionService() {
    @Inject
    lateinit var mediaSession: MediaSession
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession =
        mediaSession

    override fun onDestroy() {
        super.onDestroy()
        mediaSession.apply {
            release()
            if (this.player.playbackState != Player.STATE_IDLE) {
                this.player.apply {
                    seekTo(0)
                    playWhenReady = false
                    stop()
                }
            }
        }
    }

}