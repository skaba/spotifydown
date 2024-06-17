package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.model.Track

abstract class UrlResolver() : AppearsInFile {
    abstract fun getTracks(id: String, offset: Int?): TrackListResponse

    override fun resolveTracks(id: String): List<Track> {
        val responses: MutableList<TrackListResponse> = mutableListOf()
        var response = getTracks(id, null)
        responses.add(response)
        while (response.nextOffset != null) {
            response = getTracks(id, response.nextOffset)
            responses.add(response)
        }

        return responses
            .map(TrackListResponse::trackList)
            .flatten()
            .map { Track(it.id) }
            .toList()
    }
}