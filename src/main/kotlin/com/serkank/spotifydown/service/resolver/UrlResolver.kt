package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track

abstract class UrlResolver() : AppearsInFile {
    abstract fun getTracks(id: String, offset: Int?): TrackListResponse

    override fun resolveTracks(id: String): Sequence<Track> {
        return generateSequence(
            { getTracks(id, null) },
            { if (it.nextOffset == null) null else getTracks(id, it.nextOffset) }
        )
            .map(TrackListResponse::trackList)
            .flatten()
            .map { Track(it.id) }
    }


}