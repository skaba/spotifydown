package com.serkank.spotifydown

import com.serkank.spotifydown.dto.DownloadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.client.RestClient
import java.io.File

private val logger = KotlinLogging.logger {}

class Track(id: String, restClientBuilder: RestClient.Builder) : Downloadable(id, restClientBuilder) {

    override fun download() {
        val downloadResponse = restClientBuilder
            .build()
            .get()
            .uri("https://api.spotifydown.com/download/$id")
            .retrieve()
            .body(DownloadResponse::class.java)

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