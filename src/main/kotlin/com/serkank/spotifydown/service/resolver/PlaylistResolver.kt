package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.id
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.PLAYLIST
import com.serkank.spotifydown.runBlocking
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import xyz.gianlu.librespot.core.Session
import xyz.gianlu.librespot.metadata.PlaylistId

@Service
class PlaylistResolver(
    private val session: Session,
) : Resolver {
    override val type = PLAYLIST

    override fun resolveTracks(id: String): Flux<Track> =
        runBlocking {
            session
                .api()
                .getPlaylist(PlaylistId.fromUri("spotify:playlist:$id"))
        }.flatMapIterable { it.contents.itemsList }
            .map { Track(it.id) }
}
