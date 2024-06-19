package com.serkank.spotifydown.service.resolver

class PlaylistResolverTest : UrlResolverTest<PlaylistResolver>(PlaylistResolver::class) {
    override fun getType(): String {
        return "playlist"
    }

}