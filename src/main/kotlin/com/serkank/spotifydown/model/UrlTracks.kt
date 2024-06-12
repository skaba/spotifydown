package com.serkank.spotifydown.model

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.web.client.RestClient

abstract class UrlTracks(id: String, restClientBuilder: RestClient.Builder, spotifyDownService: SpotifyDownService) :
    Tracks(id, restClientBuilder, spotifyDownService) {

    abstract fun getTracks(offset: Int?): TrackListResponse

    override fun resolveTracks(): List<Track> {
        val responses: MutableList<TrackListResponse> = mutableListOf()
        var response = getTracks(null)
        responses.add(response)
        while (response.nextOffset != null) {
            response = getTracks(response.nextOffset)
            responses.add(response)
        }

        return responses
            .stream()
            .flatMap { r -> r.trackList.stream() }
            .map { t -> Track(t.id, restClientBuilder, spotifyDownService) }
            .toList()
    }

    fun offset(offset: Int?): String {
        return if (offset != null) "?offset=$offset" else ""
    }
}