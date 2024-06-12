package com.serkank.spotifydown.model

import com.serkank.spotifydown.service.SpotifyDownService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.client.RestClient

private val logger = KotlinLogging.logger {}

abstract class Tracks(id: String, restClientBuilder: RestClient.Builder, spotifyDownService: SpotifyDownService) :
    Downloadable(id, restClientBuilder, spotifyDownService) {
    abstract fun resolveTracks(): List<Track>

    override fun download(dryRun: Boolean) {
        val tracks = resolveTracks()
        logger.info { "Downloading ${tracks.size} tracks" }
        for (track: Track in tracks) {
            track.download(dryRun)
        }
    }
}