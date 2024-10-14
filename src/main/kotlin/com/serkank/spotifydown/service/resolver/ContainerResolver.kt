package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.service.SpotifyDownService
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

abstract class ContainerResolver(
    private val spotifyDownService: SpotifyDownService,
) : AppearsInFile {
    override fun resolveTracks(id: String): Sequence<Track> =
        generateSequence(
            { getTracks(id, null) },
            { if (it.nextOffset == null) null else getTracks(id, it.nextOffset) },
        ).map(TrackListResponse::trackList)
            .flatten()
            .distinct()
            .map(TrackList::id)
            .map(::Track)

    private fun getTracks(
        id: String,
        offset: Int?,
    ): TrackListResponse = spotifyDownService.getTracks(getType().toString(), id, offset)
}
