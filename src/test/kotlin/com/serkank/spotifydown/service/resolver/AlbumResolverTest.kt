package com.serkank.spotifydown.service.resolver

class AlbumResolverTest : UrlResolverTest<AlbumResolver>(AlbumResolver::class) {
    override fun getType(): String = "album"
}
