package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.PLAYLIST
import com.serkank.spotifydown.service.SpotifyDownService
import org.springframework.stereotype.Service

@Service
class PlaylistResolver(
    spotifyDownService: SpotifyDownService,
) : ContainerResolverResolver(spotifyDownService) {
    override fun getType(): Type = PLAYLIST
}
