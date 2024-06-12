package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class CompositeResolver(private val resolvers: List<AppearsInFile>) {
    fun resolveTracks(type: Type, id: String): List<Track> {
        logger.info { "$resolvers" }
        return resolvers.find { resolver -> resolver.getType() == type }!!.resolveTracks(id)
    }
}