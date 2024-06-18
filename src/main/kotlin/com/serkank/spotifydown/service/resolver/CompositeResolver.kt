package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import org.springframework.stereotype.Service

@Service
class CompositeResolver(private val resolvers: List<AppearsInFile>) {
    fun resolveTracks(type: Type, id: String): Sequence<Track> {
        return resolvers.find { it.getType() == type }!!.resolveTracks(id)
    }
}