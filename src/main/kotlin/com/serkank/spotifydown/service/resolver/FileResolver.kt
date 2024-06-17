package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.FILE
import com.serkank.spotifydown.model.Url
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileResolver(private val compositeResolver: CompositeResolver) : Resolver {
    override fun getType(): Type {
        return FILE
    }

    override fun resolveTracks(id: String): List<Track> {
        val file = File(id)
        val tracks = file
            .readLines()
            .map(String::trim)
            .filter(String::isNotBlank)
            .flatMap {
                val (type, trackId) = Url(it)
                compositeResolver.resolveTracks(type, trackId)
            }
            .toList()

        return tracks
    }
}