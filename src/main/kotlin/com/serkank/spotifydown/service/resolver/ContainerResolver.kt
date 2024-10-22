package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.service.SpotifyDownService
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.empty
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

abstract class ContainerResolver(
    private val spotifyDownService: SpotifyDownService,
) : AppearsInFile {
    override fun resolveTracks(id: String): Flux<Track> =
        getTracks(id, null)
            .expand { if (it.nextOffset == null || it.nextOffset == 0) empty() else getTracks(id, it.nextOffset) }
            .flatMapIterable(TrackListResponse::trackList)
            .distinct()
            .map(TrackList::id)
            .map(::Track)
            .doOnError { e -> logger.error { "Error resolving tracks of $type #$id, reason: ${e.message}" } }

    private fun getTracks(
        id: String,
        offset: Int?,
    ): Mono<TrackListResponse> = spotifyDownService.getTracks(type.toString(), id, offset)
}
