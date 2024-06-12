package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.ALBUM
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.stereotype.Service

@Service
class AlbumResolver(private val spotifyDownService: SpotifyDownService) : UrlResolver() {
    override fun getTracks(id: String, offset: Int?): TrackListResponse {
        return spotifyDownService.getAlbumTracks(id, offset)
    }

    override fun getType(): Type {
        return ALBUM
    }
}