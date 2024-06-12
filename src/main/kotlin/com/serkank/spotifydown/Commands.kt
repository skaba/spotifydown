package com.serkank.spotifydown

import com.serkank.spotifydown.model.*
import com.serkank.spotifydown.service.SpotifyDownService
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import org.springframework.web.client.RestClient

private val logger = KotlinLogging.logger {}

@Command
class Commands(private val restClientBuilder: RestClient.Builder, private val spotifyDownService: SpotifyDownService) {

    @Command
    fun download(
        @Pattern(regexp = ALL_URL_PATTERN, message = "Not a valid Spotify URL") url: String,
        @Option(longNames = ["dry-run"]) dryRun: Boolean = false
    ) {
        logger.info { "Downloading $url" }
        val matchResult = ALL_URL_PATTERN.toRegex().find(url)
        val type = enumValueOf<Type>(matchResult?.groupValues?.get(1).toString().uppercase())
        val id = matchResult?.groupValues?.get(2)!!

        val downloadable = when (type) {
            Type.TRACK -> Track(id, restClientBuilder, spotifyDownService)
            Type.ALBUM -> Album(id, restClientBuilder, spotifyDownService)
            Type.PLAYLIST -> Playlist(id, restClientBuilder, spotifyDownService)
        }

        downloadable.download(dryRun)
    }

    @Command
    fun downloadFile(filename: String, deleteAfter: Boolean = false) {
        logger.info { "Downloading from $filename" }
        FileTracks(filename, restClientBuilder, spotifyDownService, deleteAfter).download(false)
    }
}