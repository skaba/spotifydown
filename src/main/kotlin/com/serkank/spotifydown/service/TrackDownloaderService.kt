package com.serkank.spotifydown.service

import com.serkank.spotifydown.MISSING_FILE
import com.serkank.spotifydown.model.Track
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.io.File

private val logger = KotlinLogging.logger {}

@Service
class TrackDownloaderService(
    private val spotifyDownService: SpotifyDownService,
    private val restClientBuilder: RestClient.Builder
) {

    fun download(tracks: List<Track>, dryRun: Boolean) {
        logger.info { "Downloading ${tracks.size} tracks" }
        for (track: Track in tracks) {
            download(track, dryRun)
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
                    MISSING_FILE.appendText("https://open.spotify.com/track/${track.id}${System.lineSeparator()}")
                } else {
                    response.body.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                }
            }
    }
}