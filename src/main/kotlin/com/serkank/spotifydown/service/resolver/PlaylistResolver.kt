package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.PLAYLIST
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.stereotype.Service

@Service
class PlaylistResolver(
    spotifyDownService: SpotifyDownService,
) : UrlResolver(spotifyDownService) {
    override fun getType(): Type = PLAYLIST
}
