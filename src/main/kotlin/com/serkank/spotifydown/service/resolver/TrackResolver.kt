package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.TRACK
import org.springframework.stereotype.Service

@Service
class TrackResolver : AppearsInFile {
    override fun getType(): Type {
        return TRACK
    }

    override fun resolveTracks(id: String): List<Track> {
        return listOf(Track(id))
    }
}