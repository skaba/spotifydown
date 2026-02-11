package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Url
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
class CompositeResolver {
    @Autowired
    @Lazy
    private lateinit var resolvers: List<Resolver>

    private val resolverMap: Map<Type, Resolver> by lazy {
        resolvers.associateBy(Resolver::type)
    }

    fun resolveTracks(url: Url): Sequence<Track> = resolverMap[url.type]!!.resolveTracks(url.id)
}
