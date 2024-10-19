package com.serkank.spotifydown.service

import com.serkank.spotifydown.dto.DownloadResponse
import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.util.function.Tuple2
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService(
    private val spotifyDownService: SpotifyDownService,
    private val webClientBuilder: WebClient.Builder,
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
                        logMissing(track)
                    }
            }.count()
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
    ): Mono<Track> =
        getFilename(track)
            .flatMap { (url, filename) ->
                val path = Paths.get(filename!!)
                if (path.exists()) {
                    logger.info { "$path already downloaded, skipping" }
                    empty<Track>()
                }
                logger.info { "Downloading track $path" }
                if (dryRun) {
                    empty<Track>()
                }

                webClientBuilder
                    .build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntityFlux(DataBuffer::class.java)
                    .flatMap {
                        if (it.headers.contentLength == 0L) {
                            logger.error { "Server returned empty response for $path" }
                            logMissing(track)
                        } else {
                            DataBufferUtils
                                .write(it.body!!, path, StandardOpenOption.CREATE)
                                .then(just(track))
                        }
                    }.onErrorResume { e ->
                        logger.error { "Error downloading $path, reason: ${e.message}" }
                        logMissing(track)
                    }
            }

    fun getFilename(track: Track): Mono<Tuple2<String, String?>> {
        val url =
            spotifyDownService
                .download(track.id)
                .doOnError { e -> logger.error { "Error requesting download info for track ${track.url()}, reason: ${e.message}" } }
                .map(DownloadResponse::link)
                .cache()

        val filename =
            url
                .flatMap {
                    webClientBuilder
                        .build()
                        .head()
                        .uri(it)
                        .retrieve()
                        .toBodilessEntity()
                        .doOnError { e -> logger.error { "Error requesting filename info for track ${track.url()}, reason: ${e.message}" } }
                }.map { it.headers.contentDisposition.filename }

        return Mono.zip(url, filename)
    }
}
