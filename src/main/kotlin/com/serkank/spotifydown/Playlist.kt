package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

class Playlist(id: String, restClientBuilder: RestClient.Builder) : UrlTracks(id, restClientBuilder) {
    override fun getTracks(offset: Int?): TrackListResponse {

        return restClientBuilder
            .build()
            .get()
            .uri("https://api.spotifydown.com/trackList/playlist/$id${offset(offset)}")
            .retrieve()
            .toEntity<TrackListResponse>()
            .body!!
    }
}