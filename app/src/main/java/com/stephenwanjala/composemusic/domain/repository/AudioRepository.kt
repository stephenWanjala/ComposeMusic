package com.stephenwanjala.composemusic.domain.repository

import com.stephenwanjala.composemusic.domain.model.ModelAudio

interface AudioRepository {
    fun audioDataList(): List<ModelAudio>
}