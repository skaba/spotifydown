package com.serkank.spotifydown.service.resolver

class AlbumResolverTest : ContainerResolverTest<AlbumResolver>(::AlbumResolver) {
    override val type: String = "album"
}
