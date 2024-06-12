package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.web.client.RestClient

class Album(id: String, restClientBuilder: RestClient.Builder, spotifyDownService: SpotifyDownService) :
    UrlTracks(id, restClientBuilder, spotifyDownService) {
    override fun getTracks(offset: Int?): TrackListResponse {
        return spotifyDownService.getAlbumTracks(id, offset)
    }
}