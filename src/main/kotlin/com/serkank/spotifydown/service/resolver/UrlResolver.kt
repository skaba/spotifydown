package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.service.SpotifyDownService
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.empty
import reactor.core.publisher.Mono

abstract class UrlResolver(
    private val spotifyDownService: SpotifyDownService,
) : AppearsInFile {
    override fun resolveTracks(id: String): Flux<Track> =
        getTracks(id, null)
            .expand { if (it.nextOffset == null) empty() else getTracks(id, it.nextOffset) }
            .flatMapIterable(TrackListResponse::trackList)
            .distinct()
            .map { Track(it.id) }

    private fun getTracks(
        id: String,
        offset: Int?,
    ): Mono<TrackListResponse> = spotifyDownService.getTracks(getType().toString(), id, offset)
}
