package com.stephenwanjala.composemusic.data.local.datasource

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import com.stephenwanjala.composemusic.domain.model.ModelAudio
import dagger.hilt.android.qualifiers.ApplicationContext

class AudioSource(@ApplicationContext private val context: Context) {
    private val projection: Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.ALBUM,
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns._ID,
    )
    private val sortOrder: String = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"
    private val selectionClause: String = "${MediaStore.Audio.AudioColumns.IS_MUSIC} =?"
    private val selectionArguments: Array<String> = arrayOf("1")

    private val audioList = mutableListOf<ModelAudio>()

    @WorkerThread
    fun getAudioData(): List<ModelAudio> {
        context.contentResolver.query(
            /* uri = */ MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            /* projection = */ projection,
            /* selection = */ selectionClause,
            /* selectionArgs = */ selectionArguments,
            /* sortOrder = */ sortOrder

        ).use { cursor ->
            cursor?.let { mCursor ->
                if (mCursor.count > 0) {
                    val idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                    val artistColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                    val albumArtistColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
                    val albumColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                    val displayNameColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                    val dataColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
                    val tittleColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                    val durationColumn =
                        mCursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                    mCursor.moveToFirst()
                    while (mCursor.moveToNext()) {
                        val displayName = mCursor.getString(displayNameColumn)
                        val album = mCursor.getString(albumColumn)
                        val id = mCursor.getLong(idColumn)
                        val uri = ContentUris.withAppendedId(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        val tittle = mCursor.getString(tittleColumn)
                        val duration = mCursor.getString(durationColumn)
                        val data = mCursor.getString(dataColumn)
                        val artist = mCursor.getString(artistColumn)
                        val albumArtist = mCursor.getString(albumArtistColumn)

                        audioList.add(
                            ModelAudio(
                                uri = uri,
                                displayName = displayName,
                                album = album,
                                tittle = tittle,
                                data = data,
                                albumArtist = albumArtist,
                                duration = duration,
                                artist = artist
                            )
                        )
                    }
                }
            }
        }

        return audioList
    }
}