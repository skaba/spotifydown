package com.serkank.spotifydown.model

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.web.client.RestClient

class Playlist(id: String, restClientBuilder: RestClient.Builder, spotifyDownService: SpotifyDownService) :
    UrlTracks(id, restClientBuilder, spotifyDownService) {
    override fun getTracks(offset: Int?): TrackListResponse {
        return spotifyDownService.getPlaylistTracks(id, offset)
    }
}