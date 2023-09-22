package com.stephenwanjala.composemusic.data.player.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.stephenwanjala.composemusic.R
import javax.inject.Inject

@UnstableApi class ComposeMusicNotificationManager @Inject constructor(
    private val context: Context,
    private val player: ExoPlayer
) {

    init {
        createNotificationChannel()
    }

    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id = */ NOTIFICATION_CHANNEL_ID,
                /* name = */
                NOTIFICATION_CHANNEL_NAME, /* importance = */
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun startNotificationService(
        mediaSession: MediaSession,
        mediaSessionService: MediaSessionService
    ) {
        buildNotification(context, mediaSession)
    }


    @UnstableApi
    private fun buildNotification(context: Context, mediaSession: MediaSession) {
        PlayerNotificationManager.Builder(context, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .apply {
                setMediaDescriptionAdapter(
                    MediaDescriptionAdapter(
                        context = context,
                        pendingIntent = mediaSession.sessionActivity
                    )
                )
                setSmallIconResourceId(R.drawable.music_note)
                setNextActionIconResourceId(R.drawable.skip_next)
                setPreviousActionIconResourceId(R.drawable.skip_previous)
                setPlayActionIconResourceId(R.drawable.play_arrow)
                setPauseActionIconResourceId(R.drawable.pause)
            }
            .build()
            .apply {
                setUseFastForwardAction(true)
                setUseFastForwardAction(true)
                setMediaSessionToken(mediaSession.sessionCompatToken)
                setUseFastForwardActionInCompactView(true)
                setUsePreviousActionInCompactView(true)
                setUsePlayPauseActions(true)
                setUseStopAction(true)
                setPriority(NotificationCompat.PRIORITY_LOW)
                setPlayer(player)
            }
    }

    @UnstableApi
    private inner class MediaDescriptionAdapter(
        private val context: Context,
        private val pendingIntent: PendingIntent?
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence =
            player.mediaMetadata.albumTitle ?: "Unknown"

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            pendingIntent

        override fun getCurrentContentText(player: Player): CharSequence =
            player.mediaMetadata.displayTitle ?: "Unknown"

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            Glide.with(context)
                .asBitmap()
                .load(player.mediaMetadata.artworkUri)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit

                })
            return null
        }
    }


    private fun startComposeMusicServiceNotificationForeGround(mediaSessionService: MediaSessionService){

    }
    private companion object {
        const val NOTIFICATION_ID = 2319
        const val NOTIFICATION_CHANNEL_ID = "Compose Music_1"
        const val NOTIFICATION_CHANNEL_NAME = "Compose Music"
    }
}