package com.serkank.spotifydown.service

import com.serkank.spotifydown.filename
import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import com.spotify.metadata.Metadata
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.IOUtils
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.id3.ID3v24Tag
import org.springframework.stereotype.Service
import vavi.sound.sampled.mp3.MpegAudioFileWriter
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.TrackId
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService(
    private val session: Session,
) {
    fun download(
        tracks: Sequence<Track>,
        dryRun: Boolean,
    ): Int {
        logger.info { "Downloading tracks" }
        return runBlocking(Dispatchers.IO) {
            tracks
                .map {
                    async {
                        download(it, dryRun)
                    }
                }.toList()
                .awaitAll()
                .count { it }
        }
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
    ): Boolean {
        val metaData = getMetaData(track) ?: return false
        val filename = metaData.filename
        val file = File(filename)
        if (file.exists()) {
            logger.info { "$file already downloaded, skipping" }
            return false
        }
        logger.info { "Downloading track $file" }
        if (dryRun) {
            return false
        }
        try {
            val input =
                session
                    .contentFeeder()
                    .load(
                        TrackId.fromBase62(track.id),
                        { it.first { it.format == Metadata.AudioFile.Format.OGG_VORBIS_320 } },
                        false,
                        null,
                    ).`in`
                    .stream()
            val ogg = AudioSystem.getAudioInputStream(input)
            val output = file.outputStream()
            val pcmFormat = AudioFormat(44100f, 16, ogg.format.channels, true, false)
            val mp3AudioFormat =
                AudioFormat(
                    MpegAudioFileWriter.MPEG1L3,
                    -1f,
                    32,
                    pcmFormat.channels,
                    -1,
                    -1f,
                    false,
                    mapOf("bitrate" to "320"),
                )
            val pcmInputStream = AudioSystem.getAudioInputStream(pcmFormat, ogg)
            val mp3InputStream = AudioSystem.getAudioInputStream(mp3AudioFormat, pcmInputStream)
            AudioSystem.write(mp3InputStream, MpegAudioFileWriter.MP3, output)
            IOUtils.closeQuietly(output, mp3InputStream, pcmInputStream, input)
            tagFile(file, metaData)
            logger.info { "Downloaded $filename" }
            return true
        } catch (e: Exception) {
            logger.debug(e) { "Error downloading ${track.url}" }
            logger.error { "Error downloading $filename: ${e.message}" }
            logMissing(track)
            return false
        }
    }

    private fun tagFile(
        mp3: File,
        metadata: Metadata.Track,
    ) {
        val mP3File = MP3File(mp3)
        val tag = ID3v24Tag()
        tag.setField(FieldKey.ARTIST, metadata.artistList[0].name)
        tag.setField(FieldKey.ALBUM_ARTISTS, *metadata.artistList.map { it.name }.toTypedArray())
        tag.setField(FieldKey.TITLE, metadata.name)
        tag.setField(FieldKey.ALBUM, metadata.album.name)
        mP3File.iD3v2Tag = tag
        mP3File.save()
    }

    private fun getMetaData(track: Track): Metadata.Track? {
        val trackId = TrackId.fromBase62(track.id)
        try {
            return session
                .api()
                .getMetadata4Track(trackId)
        } catch (e: Exception) {
            logger.debug(e) { "Error getting metadata for ${track.url}" }
            logger.error { "Error getting metadata for ${track.url}: ${e.message}" }
            logMissing(track)
            return null
        }
    }
}
