package com.serkank.spotifydown.service

import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import com.spotify.metadata.Metadata.AudioFile
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import reactor.kotlin.core.util.function.*
import xyz.gianlu.librespot.audio.HaltListener
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.PlaylistId
import xyz.gianlu.librespot.metadata.TrackId
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService(
    private val spotifyDownService: SpotifyDownService,
    private val webClientBuilder: WebClient.Builder,
    private val session: Session,
) {
    fun download(
        tracks: Flux<Track>,
        dryRun: Boolean,
    ): Mono<Long> {
        logger.info { "Downloading tracks" }
        return tracks
            .flatMap { track ->
                download(track, dryRun)
                    .onErrorResume { e ->
                        logger.error { "Error downloading track ${track.url()}, reason: ${e.message}" }
                        return@onErrorResume logMissing(track)
                    }
            }.count()
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
    ): Mono<Track> =
        getDownloadInfo(track)
            .flatMap { filename ->
                val path = Paths.get(filename)
                if (path.exists()) {
                    logger.info { "$path already downloaded, skipping" }
                    empty<Track>()
                }
                logger.info { "Downloading track $path" }
                if (dryRun) {
                    empty<Track>()
                }

                val input =
                    session
                        .contentFeeder()
                        .load(
                            TrackId.fromBase62(track.id),
                            { it.first { it.format == AudioFile.Format.OGG_VORBIS_320 } },
                            false,
                            MyHaltListener(),
                        ).`in`
                        .stream()

                val buffer =
                    DataBufferUtils
                        .readInputStream({ input }, DefaultDataBufferFactory(), 1024 * 1024)
                DataBufferUtils
                    .write(buffer, path, StandardOpenOption.CREATE)
                    .doFinally { input.close() }
                    .then(just(track))

                /*Mono
                    .fromSupplier {
                        session
                            .contentFeeder()
                            .load(
                                TrackId.fromBase62(track.id),
                                { it.first { it.format == AudioFile.Format.OGG_VORBIS_320 } },
                                false,
                                null,
                            )
                    }.map { it.`in`.stream() }
                    .map { DataBufferUtils.readInputStream({ it }, DefaultDataBufferFactory(), 1024 * 1024) }
                    .map { DataBufferUtils.write(it, path, StandardOpenOption.CREATE) }
                    .then(just(track))*/
            }

    fun getDownloadInfo(track: Track): Mono<String> {
        val trackId = TrackId.fromBase62(track.id)
        session.api().getPlaylist(PlaylistId.)
        return Mono
            .fromSupplier { session.api().getMetadata4Track(trackId) }
            // .subscribeOn(Schedulers.boundedElastic())
            .map { "${it.artistList.joinToString { it.name }} - ${it.name}.ogg" }
    }

    class MyHaltListener : HaltListener {
        override fun streamReadHalted(
            chunk: Int,
            time: Long,
        ) {
            print("Read halted")
        }

        override fun streamReadResumed(
            chunk: Int,
            time: Long,
        ) {
            println("Read resumed")
        }
    }
}
