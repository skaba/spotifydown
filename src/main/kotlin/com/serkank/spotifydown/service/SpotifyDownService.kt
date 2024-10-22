package com.serkank.spotifydown.service

import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface SpotifyDownService {
    @GetExchange("/trackList/{type}/{id}")
    fun getTracks(
        @PathVariable type: String,
        @PathVariable id: String,
        @RequestParam(required = false) offset: Int?,
    ): TrackListResponse
}
