package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.FILE
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileResolver(
    private val compositeResolver: CompositeResolver,
) : Resolver {
    override val type: Type = FILE

    override fun resolveTracks(id: String): Sequence<Track> =
        File(id)
            .bufferedReader()
            .lineSequence()
            .mapToTracks(compositeResolver)
}
