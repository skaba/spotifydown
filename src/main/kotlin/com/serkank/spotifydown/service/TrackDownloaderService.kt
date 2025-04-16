package com.serkank.spotifydown.service

import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.runBlocking
import com.spotify.metadata.Metadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.io.IOUtils
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
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
        tracks: Flux<Track>,
        dryRun: Boolean,
    ): Mono<Long> {
        logger.info { "Downloading tracks" }
        return tracks
            .flatMap { track ->
                download(track, dryRun, session)
                    .onErrorResume { e ->
                        logger.debug(e) { "Error downloading track ${track.url}, reason: ${e.message}" }
                        logger.error { "Error downloading track ${track.url}, reason: ${e.message}" }
                        logMissing(track)
                    }
            }.count()
            .doFinally { session.close() }
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
        session: Session,
    ): Mono<Track> {
        if (dryRun) {
            return empty()
        }

        return getFilename(track)
            .flatMap { filename ->
                val file = File(filename)
                if (file.exists()) {
                    logger.info { "$file already downloaded, skipping" }
                    empty<Track>()
                }
                logger.info { "Downloading track $file" }
                if (dryRun) {
                    empty<Track>()
                }

                runBlocking {
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
                    val output = file.outputStream()
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
                    logger.info { "Downloaded $filename" }
                }.map { track }
                    .onErrorResume { e ->
                        logger.debug(e) { "Error downloading $file, reason: ${e.message}" }
                        logger.error { "Error downloading $file, reason: ${e.message}" }
                        logMissing(track)
                    }
            }
    }

    private fun getFilename(track: Track): Mono<String> {
        val trackId = TrackId.fromBase62(track.id)

        return runBlocking {
            session
                .api()
                .getMetadata4Track(trackId)
                .let { "${it.artistList.joinToString { it.name }} - ${it.name}.mp3" }
        }.doOnError { e ->
            logger.debug(e) { "Error downloading ${track.url}" }
            logger.error { "Error downloading ${track.url}" }
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
