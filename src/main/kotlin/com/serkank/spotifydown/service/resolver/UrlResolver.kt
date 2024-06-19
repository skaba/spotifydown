package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.service.SpotifyDownService

abstract class UrlResolver(private val spotifyDownService: SpotifyDownService) : AppearsInFile {
    override fun resolveTracks(id: String): Sequence<Track> {
        return generateSequence(
            { getTracks(id, null) },
            { if (it.nextOffset == null) null else getTracks(id, it.nextOffset) }
        )
            .map(TrackListResponse::trackList)
            .flatten()
            .map { Track(it.id) }
    }

    private fun getTracks(id: String, offset: Int?): TrackListResponse {
        return spotifyDownService.getTracks(getType().toString(), id, offset)
    }

}