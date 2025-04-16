package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.id
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.ALBUM
import org.springframework.stereotype.Service
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.AlbumId

@Service
class AlbumResolver(
    private val session: Session,
) : AppearsInFile {
    override val type = ALBUM

    override fun resolveTracks(id: String): Sequence<Track> =
        session
            .api()
            .getMetadata4Album(AlbumId.fromBase62(id))
            .discList
            .asSequence()
            .flatMap { it.trackList }
            .map {
                Track(it.id)
            }
}
