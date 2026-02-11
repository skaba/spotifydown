package com.serkank.spotifydown.service

import com.serkank.spotifydown.filename
import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.runBlocking
import com.spotify.metadata.Metadata
import io.github.oshai.kotlinlogging.KotlinLogging
import org.apache.commons.io.IOUtils
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.id3.ID3v24Tag
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
            .flatMap({ track ->
                download(track, dryRun, session)
                    .onErrorResume { e ->
                        logger.debug(e) { "Error downloading track ${track.url}, reason: ${e.message}" }
                        logger.error { "Error downloading track ${track.url}, reason: ${e.message}" }
                        logMissing(track)
                    }
            }, 5)
            .count()
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
        session: Session,
    ): Mono<Track> =
        getMetadata(track)
            .flatMap { metadata ->
                val filename = metadata.filename
                val file = File(filename)
                if (file.exists()) {
                    logger.info { "$file already downloaded, skipping" }
                    return@flatMap empty()
                }
                logger.info { "Downloading track $file" }
                if (dryRun) {
                    return@flatMap empty()
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
                    tagFile(file, metadata)
                    logger.info { "Downloaded $filename" }
                }.map { track }
                    .onErrorResume { e ->
                        logger.debug(e) { "Error downloading $file" }
                        logger.error { "Error downloading $file: ${e.message}" }
                        return@onErrorResume logMissing(track)
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

    private fun getMetadata(track: Track): Mono<Metadata.Track> {
        val trackId = TrackId.fromBase62(track.id)
        return runBlocking {
            session
                .api()
                .getMetadata4Track(trackId)
        }.doOnError { e ->
            logger.debug(e) { "Error getting metadata for ${track.url}" }
            logger.error { "Error getting metadata for ${track.url}: ${e.message}" }
        }
    }
}
