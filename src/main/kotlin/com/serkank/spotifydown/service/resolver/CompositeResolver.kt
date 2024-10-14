package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Url
import org.springframework.stereotype.Service

@Service
class CompositeResolver(
    resolvers: List<AppearsInFile>,
) {
    private final val resolvers: Map<Type, AppearsInFile> = resolvers.associateBy(AppearsInFile::getType)

    fun resolveTracks(url: Url): Sequence<Track> = resolvers[url.type]!!.resolveTracks(url.id)
}
