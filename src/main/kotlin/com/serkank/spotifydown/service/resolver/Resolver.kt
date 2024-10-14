package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type

interface Resolver {
    fun getType(): Type

    fun resolveTracks(id: String): Sequence<Track>
}
