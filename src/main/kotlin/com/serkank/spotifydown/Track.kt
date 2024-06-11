package com.serkank.spotifydown
import com.serkank.spotifydown.dto.DownloadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.apache.hc.core5.http.HttpHeaders
import java.io.File

private val logger = KotlinLogging.logger {}

class Track(id: String) : Downloadable(id) {

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

    override fun download() {
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
    }
}