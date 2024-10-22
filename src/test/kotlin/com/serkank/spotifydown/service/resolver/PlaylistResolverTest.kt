package com.serkank.spotifydown.service.resolver

class PlaylistResolverTest : ContainerResolverTest<PlaylistResolver>(::PlaylistResolver) {
    override val type: String = "playlist"
}
