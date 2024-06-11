package com.serkank.spotifydown

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.web.client.RestClient

private val logger = KotlinLogging.logger {}

abstract class Tracks(id: String, restClientBuilder: RestClient.Builder) : Downloadable(id, restClientBuilder) {
    abstract fun resolveTracks(): List<Track>

    override fun download() {
        val tracks = resolveTracks()
        logger.info { "Downloading ${tracks.size} tracks" }
        for (track: Track in tracks) {
            track.download()
        }
    }
}