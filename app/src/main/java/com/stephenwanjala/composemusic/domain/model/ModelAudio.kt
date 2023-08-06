package com.stephenwanjala.composemusic.domain.model

import android.net.Uri

data class ModelAudio(
    val uri: Uri,
    val displayName: String,
    val tittle: String,
    val album: String,
    val albumArtist: String,
    val data: String,
    val duration: String,
    val artist: String
)
