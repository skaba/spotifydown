package com.serkank.spotifydown

import org.springframework.web.client.RestClient
import java.io.File

class FileTracks(id: String, restClientBuilder: RestClient.Builder, private val deleteAfter: Boolean) :
    Tracks(id, restClientBuilder) {
    override fun resolveTracks(): List<Track> {
        val file = File(id)
        val tracks = file
            .readLines()
            .stream()
            .map(String::trim)
            .filter(String::isNotBlank)
            .map { id -> Track(id, restClientBuilder) }
            .toList()
        if (deleteAfter) {
            file.delete()
        }
        return tracks
    }
}