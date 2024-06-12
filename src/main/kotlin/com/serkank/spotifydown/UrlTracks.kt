package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

abstract class UrlTracks(id: String, restClientBuilder: RestClient.Builder) : Tracks(id, restClientBuilder) {

    abstract fun getUrl(offset: Int?): String

    fun getTracks(offset: Int?): TrackListResponse {

        return restClientBuilder
            .build()
            .get()
            .uri(getUrl(offset))
            .retrieve()
            .toEntity<TrackListResponse>()
            .body!!
    }

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