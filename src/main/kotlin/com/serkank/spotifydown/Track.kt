package com.serkank.spotifydown

import com.serkank.spotifydown.dto.DownloadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.client.RestClient
import java.io.File

private val logger = KotlinLogging.logger {}

class Track(id: String, restClientBuilder: RestClient.Builder) : Downloadable(id, restClientBuilder) {

    /*init {
        val response = request("https://api.spotifydown.com/download/$id")

            .execute()
            .returnContent()
            .asString()
        val downloadResponse = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<DownloadResponse>(response)
        file = File("${downloadResponse.metadata.artists} - ${downloadResponse.metadata.title}.mp3")
        url = downloadResponse.link
    }*/

    /*override fun download() {
        val response = request("https://api.spotifydown.com/download/$id")

            .execute()
            .returnContent()
            .asString()
        val downloadResponse = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<DownloadResponse>(response)
        val file = File("${downloadResponse.metadata.artists} - ${downloadResponse.metadata.title}.mp3")
        val url = downloadResponse.link
        logger.info { "Downloading track ${file.path}" }
        request(url)
            .execute()
            .saveContent(file)
    }*/
    override fun download() {
        val downloadResponse = restClientBuilder
            .build()
            .get()
            .uri("https://api.spotifydown.com/download/$id")
            .retrieve()
            .body(DownloadResponse::class.java)
        val fileName =
            "${downloadResponse?.metadata?.artists} - ${downloadResponse?.metadata?.title}.mp3".replace('/', '_')
        val file = File(fileName)
        if (file.exists()) {
            logger.info { "${file.path} already downloaded, skipping" }
            return
        }
        val url = downloadResponse?.link
        logger.info { "Downloading track ${file.path}" }

        restClientBuilder
            .build()
            .get()
            .uri(url!!)
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