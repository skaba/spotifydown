package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Type.ALBUM
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.stereotype.Service

@Service
class AlbumResolver(
    spotifyDownService: SpotifyDownService,
) : ContainerResolver(spotifyDownService) {
    override val type = ALBUM
}
