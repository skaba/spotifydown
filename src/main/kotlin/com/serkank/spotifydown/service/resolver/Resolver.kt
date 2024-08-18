package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import reactor.core.publisher.Flux

interface Resolver {
    fun getType(): Type

    fun resolveTracks(id: String): Flux<Track>
}
