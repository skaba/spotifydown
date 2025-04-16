package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.id
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.ALBUM
import com.serkank.spotifydown.runBlocking
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.AlbumId

@Service
class AlbumResolver(
    private val session: Session,
) : AppearsInFile {
    override val type = ALBUM

    override fun resolveTracks(id: String): Flux<Track> =
        runBlocking {
            session
                .api()
                .getMetadata4Album(AlbumId.fromBase62(id))
        }.flatMapIterable { it.discList }.flatMapIterable { it.trackList }.map { Track(it.id) }
}
