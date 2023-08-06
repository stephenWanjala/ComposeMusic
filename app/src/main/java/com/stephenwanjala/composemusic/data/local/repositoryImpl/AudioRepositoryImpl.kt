package com.stephenwanjala.composemusic.data.local.repositoryImpl

import com.stephenwanjala.composemusic.data.local.datasource.AudioSource
import com.stephenwanjala.composemusic.domain.model.ModelAudio
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val audioSource: AudioSource
) : AudioRepository {
    override fun audioDataList(): List<ModelAudio> = audioSource.getAudioData()
}