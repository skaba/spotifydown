package com.serkank.spotifydown.service.resolver

class PlaylistResolverTest : ContainerResolverTest<PlaylistResolver>(PlaylistResolver::class) {
    override val type: String = "playlist"
}
