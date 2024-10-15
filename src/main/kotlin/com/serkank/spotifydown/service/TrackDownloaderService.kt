package com.serkank.spotifydown.service

import com.serkank.spotifydown.model.Track
import com.spotify.metadata.Metadata
import io.github.oshai.kotlinlogging.KotlinLogging
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
        return tracks
            .map { download(it, dryRun) }
            .filter { it }
            .count()
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
    ): Boolean {
        val filename = getFilename(track)
        val path = File(filename)
        if (path.exists()) {
            logger.info { "$path already downloaded, skipping" }
            return false
        }
        logger.info { "Downloading track $path" }
        if (dryRun) {
            return false
        }
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
        return true
    }

    fun getFilename(track: Track): String {
        val trackId = TrackId.fromBase62(track.id)
        return session.api().getMetadata4Track(trackId).let { "${it.artistList.joinToString { it.name }} - ${it.name}.mp3" }
    }
}
