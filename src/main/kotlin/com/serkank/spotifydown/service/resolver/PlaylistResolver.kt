package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.PLAYLIST
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.stereotype.Service

@Service
class PlaylistResolver(private val spotifyDownService: SpotifyDownService) : UrlResolver() {
    override fun getTracks(id: String, offset: Int?): TrackListResponse {
        return spotifyDownService.getPlaylistTracks(id, offset)
    }

    override fun getType(): Type {
        return PLAYLIST
    }
}