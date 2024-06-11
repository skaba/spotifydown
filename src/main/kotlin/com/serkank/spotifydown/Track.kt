package com.serkank.spotifydown
import com.serkank.spotifydown.dto.DownloadResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.serialization.json.Json
import org.apache.hc.core5.http.HttpHeaders
import org.springframework.http.HttpRequest
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction
import java.io.File
import kotlin.io.path.deleteExisting
import kotlin.io.path.deleteIfExists

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
        val fileName = "${downloadResponse?.metadata?.artists} - ${downloadResponse?.metadata?.title}.mp3".replace('/', '_')
        val file = File(fileName)
        val url = downloadResponse?.link
        logger.info { "Downloading track ${file.path}" }

        val size = restClientBuilder
            .build()
            .get()
            .uri(url!!)
            .exchange(FileWriter(file))

        if(size == 0L) {
            logger.error { "Server returned empty response for ${file.path}" }
        }

/*        do {
            val size = restClientBuilder
                .build()
                .get()
                .uri(url!!)
                .exchange(FileWriter(file))
            if(size == 0L) {
                Thread.sleep(5000L)
            }
        } while (size == 0L)*/
/*        restClientBuilder
            .build()
            .get()
            .uri(url!!)
            .exchange(FileWriter(file))*/
    }

    class FileWriter(val file: File): ExchangeFunction<Long> {
        override fun exchange(
            request: HttpRequest,
            response: RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse
        ): Long {
            if(response.headers.contentLength != 0L) {
                response.body.use { input -> file.outputStream().use { output -> input.copyTo(output) } }
            }
            return response.headers.contentLength
        }
    }

}