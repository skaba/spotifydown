package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type

interface Resolver {
    val type: Type

    fun resolveTracks(id: String): Sequence<Track>
}
