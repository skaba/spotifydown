package com.serkank.spotifydown.model

import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.web.client.RestClient

abstract class Downloadable(
    val id: String,
    val restClientBuilder: RestClient.Builder,
    val spotifyDownService: SpotifyDownService
) {
    abstract fun download(dryRun: Boolean)
}