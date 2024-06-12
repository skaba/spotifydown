package com.serkank.spotifydown.model

import com.serkank.spotifydown.MISSING_FILE
import com.serkank.spotifydown.service.SpotifyDownService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.client.RestClient
import java.io.File

private val logger = KotlinLogging.logger {}

class Track(id: String, restClientBuilder: RestClient.Builder, spotifyDownService: SpotifyDownService) :
    Downloadable(id, restClientBuilder, spotifyDownService) {

    override fun download(dryRun: Boolean) {
        val downloadResponse = spotifyDownService.download(id)

        val url = downloadResponse?.link

        val fileName = restClientBuilder
            .build()
            .head()
            .uri(url!!)
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
                    MISSING_FILE.appendText("$id${System.lineSeparator()}")
                } else {
                    response.body.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
                }
            }
    }
}