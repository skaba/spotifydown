package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Url
import org.springframework.stereotype.Service

@Service
class CompositeResolver(private val resolvers: List<AppearsInFile>) {
    fun resolveTracks(url: Url): Sequence<Track> {
        return resolvers.find { it.getType() == url.type }!!.resolveTracks(url.id)
    }
}