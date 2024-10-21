package com.serkank.spotifydown.service.resolver

class PlaylistResolverTest : ContainerResolverResolverTest<PlaylistResolver>(PlaylistResolver::class) {
    override fun getType(): String = "playlist"
}
