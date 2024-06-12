package com.serkank.spotifydown.model

import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.web.client.RestClient
import java.io.File

class FileTracks(
    id: String,
    restClientBuilder: RestClient.Builder,
    spotifyDownService: SpotifyDownService,
    private val deleteAfter: Boolean
) :
    Tracks(id, restClientBuilder, spotifyDownService) {
    override fun resolveTracks(): List<Track> {
        val file = File(id)
        val tracks = file
            .readLines()
            .stream()
            .map(String::trim)
            .filter(String::isNotBlank)
            .map { id -> Track(id, restClientBuilder, spotifyDownService) }
            .toList()
        if (deleteAfter) {
            file.delete()
        }
        return tracks
    }
}