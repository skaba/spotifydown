package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.FILE
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromStream
import reactor.core.publisher.Flux.using
import java.io.File

@Service
class FileResolver(
    private val compositeResolver: CompositeResolver,
) : Resolver {
    override fun getType(): Type = FILE

    override fun resolveTracks(id: String): Flux<Track> =
        using({ File(id).bufferedReader() }, { fromStream(it.lines()) }, { it.close() })
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
            .mapToTracks(compositeResolver)
}
