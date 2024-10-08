package com.serkank.spotifydown.service

import com.serkank.spotifydown.dto.DownloadResponse
import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import reactor.core.publisher.Mono

interface SpotifyDownService {
    @GetExchange("/download/{id}")
    fun download(
        @PathVariable id: String,
    ): Mono<DownloadResponse>

    @GetExchange("/trackList/{type}/{id}")
    fun getTracks(
        @PathVariable type: String,
        @PathVariable id: String,
        @RequestParam(required = false) offset: Int?,
    ): Mono<TrackListResponse>
}
