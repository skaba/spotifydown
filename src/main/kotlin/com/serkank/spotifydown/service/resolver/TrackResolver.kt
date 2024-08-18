package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.TRACK
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.just

@Service
class TrackResolver : AppearsInFile {
    override fun getType(): Type = TRACK

    override fun resolveTracks(id: String): Flux<Track> = just(Track(id))
}
