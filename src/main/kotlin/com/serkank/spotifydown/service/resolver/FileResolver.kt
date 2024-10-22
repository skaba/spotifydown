package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.FILE
import com.serkank.spotifydown.model.Url
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromStream
import reactor.core.publisher.Flux.using
import java.io.BufferedReader
import java.io.File

@Service
class FileResolver(
    private val compositeResolver: CompositeResolver,
) : Resolver {
    override val type = FILE

    override fun resolveTracks(id: String): Flux<Track> =
        using({ File(id).bufferedReader() }, { fromStream(it.lines()) }, BufferedReader::close)
            .map(String::trim)
            .filter(String::isNotBlank)
            .distinct()
            .map(Url::invoke)
            .mapToTracks(compositeResolver)
}
