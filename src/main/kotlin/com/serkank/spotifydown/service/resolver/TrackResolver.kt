package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.TRACK
import org.springframework.stereotype.Service

@Service
class TrackResolver : AppearsInFile {
    override val type = TRACK

    override fun resolveTracks(id: String): Sequence<Track> = sequenceOf(Track(id))
}
