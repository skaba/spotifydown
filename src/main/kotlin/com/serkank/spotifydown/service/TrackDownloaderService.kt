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
    ): Flux<Void> {
        logger.info { "Downloading tracks" }
        return tracks
            .flatMap { track ->
                download(track, dryRun)
                    .onErrorResume { e ->
                        logger.error { "Error downloading track ${track.url()}, reason: ${e.message}" }
                        return@onErrorResume logMissing(track)
                    }
            }
    }

    private fun download(
        track: Track,
        dryRun: Boolean,
    ): Mono<Void> =
        getDownloadInfo(track)
            .flatMap { (url, filename) ->
                val path = Paths.get(filename!!)
                if (path.exists()) {
                    logger.info { "$path already downloaded, skipping" }
                    return@flatMap empty()
                }
                logger.info { "Downloading track $path" }
                if (dryRun) {
                    return@flatMap empty()
                }

                return@flatMap webClientBuilder
                    .build()
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntityFlux(DataBuffer::class.java)
                    .flatMap {
                        if (it.headers.contentLength == 0L) {
                            logger.error { "Server returned empty response for $path" }
                            return@flatMap logMissing(track)
                        } else {
                            return@flatMap DataBufferUtils.write(it.body!!, path, StandardOpenOption.CREATE).then()
                        }
                    }.onErrorResume { e ->
                        logger.error { "Error downloading $path, reason: ${e.message}" }
                        return@onErrorResume logMissing(track)
                    }
            }.then()

    private fun getDownloadInfo(track: Track): Mono<Tuple2<String, String?>> {
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
