package com.serkank.spotifydown.service.resolver

class PlaylistResolverTest : ContainerResolverResolverTest<PlaylistResolver>(PlaylistResolver::class) {
    override val type: String = "playlist"
}
