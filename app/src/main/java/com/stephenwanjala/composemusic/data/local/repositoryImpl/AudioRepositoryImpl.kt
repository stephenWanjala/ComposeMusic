package com.stephenwanjala.composemusic.data.local.repositoryImpl

import com.stephenwanjala.composemusic.data.local.datasource.AudioSource
import com.stephenwanjala.composemusic.domain.model.ModelAudio
import com.stephenwanjala.composemusic.domain.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepositoryImpl @Inject constructor(
    private val audioSource: AudioSource
) : AudioRepository {
    override suspend fun audioDataList(): List<ModelAudio> = withContext( Dispatchers.IO){
         audioSource.getAudioData()
    }
}