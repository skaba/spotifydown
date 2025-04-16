package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.PLAYLIST
import org.springframework.stereotype.Service
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.PlaylistId

@Service
class PlaylistResolver(
    private val session: Session,
) : AppearsInFile {
    override val type = PLAYLIST

    override fun resolveTracks(id: String): Sequence<Track> =
        session
            .api()
            .getPlaylist(PlaylistId.fromUri("spotify:playlist:$id"))
            .contents
            .itemsList
            .asSequence()
            .map { Track(it.uri.split(':').last()) }
}
