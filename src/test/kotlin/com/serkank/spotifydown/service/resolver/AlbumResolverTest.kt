package com.serkank.spotifydown.service.resolver

class AlbumResolverTest : ContainerResolverTest<AlbumResolver>(AlbumResolver::class) {
    override fun getType(): String = "album"
}
