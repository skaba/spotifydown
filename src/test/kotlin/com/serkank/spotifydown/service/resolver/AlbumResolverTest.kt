package com.serkank.spotifydown.service.resolver

class AlbumResolverTest : ContainerResolverTest<AlbumResolver>(AlbumResolver::class) {
    override val type: String = "album"
}
