package com.serkank.spotifydown.service

import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import com.spotify.metadata.Metadata
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Service
import vavi.sound.sampled.mp3.MpegAudioFileWriter
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.TrackId
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService {
    fun download(
        tracks: Sequence<Track>,
        dryRun: Boolean,
    ): Int {
        logger.info { "Downloading tracks" }
        session().use { session ->
            return runBlocking(Dispatchers.IO) {
                tracks
                    .map {
                        async {
                            download(it, dryRun, session)
                        }
                    }.toList()
                    .awaitAll()
                    .count { it }
            }
        }
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
        session: Session,
    ): Boolean {
        val filename = getFilename(track, session) ?: return false
        val path = File(filename)
        if (path.exists()) {
            logger.info { "$path already downloaded, skipping" }
            return false
        }
        logger.info { "Downloading track $path" }
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
            AudioSystem.write(mp3InputStream, MpegAudioFileWriter.MP3, path)
            logger.info { "Downloaded $filename" }
            return true
        } catch (e: Exception) {
            logger.error { "Error downloading $filename" }
            logMissing(track)
            return false
        }
    }

    private fun getFilename(
        track: Track,
        session: Session,
    ): String? {
        val trackId = TrackId.fromBase62(track.id)
        try {
            return session
                .api()
                .getMetadata4Track(trackId)
                .let { "${it.artistList.joinToString { it.name }} - ${it.name}.mp3" }
        } catch (e: Exception) {
            logger.error { "Error downloading ${track.url()}" }
            logMissing(track)
            return null
        }
    }

    private fun session(): Session {
        // val credentialsFile =
        // credentialsFile.createNewFile()
        val conf =
            Session.Configuration
                .Builder()
                .setStoreCredentials(true)
                .setStoredCredentialsFile(File(System.getProperty("user.home"), ".spotify_down"))
                .setCacheEnabled(false)
                /*.setStoreCredentials(true)
                .setStoredCredentialsFile()
                .setTimeSynchronizationMethod()
                .setTimeManualCorrection()
                .setProxyEnabled()
                .setProxyType()
                .setProxyAddress()
                .setProxyPort()
                .setProxyAuth()
                .setProxyUsername()
                .setProxyPassword()
                .setRetryOnChunkError()                 */
                .build()
        return Session
            .Builder(conf)
            .oauth()
            .create()
    }
}
