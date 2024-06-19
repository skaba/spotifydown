package com.serkank.spotifydown.service

import com.serkank.spotifydown.logMissing
import com.serkank.spotifydown.model.Track
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import java.io.File

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService(
    private val spotifyDownService: SpotifyDownService,
    private val restClientBuilder: RestClient.Builder
) {

    fun download(tracks: Sequence<Track>, dryRun: Boolean) {
        logger.info { "Downloading tracks" }
        for (track: Track in tracks) {
            try {
                download(track, dryRun)
            } catch (e: RestClientException) {
                logger.error { "Error downloading ${e.message}" }
                logMissing(track)
            }
        }
    }

    private fun download(track: Track, dryRun: Boolean) {
        val downloadResponse = spotifyDownService.download(track.id)

        val url = downloadResponse.link

        val fileName = restClientBuilder
            .build()
            .head()
            .uri(url)
            .retrieve()
            .toBodilessEntity()
            .headers
            .contentDisposition
            .filename

        val file = File(fileName!!)
        if (file.exists()) {
            logger.info { "${file.path} already downloaded, skipping" }
            return
        }

        logger.info { "Downloading track ${file.path}" }
        if (dryRun) {
            return
        }

        restClientBuilder
            .build()
            .get()
            .uri(url)
            .exchange { _, response ->
                if (response.headers.contentLength == 0L) {
                    logger.error { "Server returned empty response for ${file.path}" }
                    logMissing(track)
                } else {
                    response.body.use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }
    }
}