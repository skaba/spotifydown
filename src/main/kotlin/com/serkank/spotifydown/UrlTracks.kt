package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.client.RestClient

abstract class UrlTracks(id: String, restClientBuilder: RestClient.Builder) : Tracks(id, restClientBuilder) {

    abstract fun getTracks(offset: Int?): TrackListResponse

    override fun resolveTracks(): List<Track> {
        val responses: MutableList<TrackListResponse> = mutableListOf()
        var response = getTracks(null)
        responses.add(response)
        while (response.nextOffset != null) {
            response = getTracks(response.nextOffset?.toInt())
            responses.add(response)
        }

        return responses
            .stream()
            .flatMap { r -> r.trackList.stream() }
            .map { t -> Track(t.id, restClientBuilder) }
            .toList()
    }

    val tracks: MutableList<Track> = mutableListOf()

    init {
        tracks.addAll(resolveTracks())
    }

    fun offset(offset: Int?): String {
        return if (offset != null) "?offset=$offset" else ""
    }
}