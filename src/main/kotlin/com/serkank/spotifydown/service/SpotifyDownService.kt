package com.serkank.spotifydown.service

import com.serkank.spotifydown.dto.DownloadResponse
import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface SpotifyDownService {
    @GetExchange("/download/{id}")
    fun download(@PathVariable id: String): DownloadResponse

    @GetExchange("/trackList/playlist/{id}")
    fun getPlaylistTracks(@PathVariable id: String, @RequestParam(required = false) offset: Int?): TrackListResponse

    @GetExchange("/trackList/album/{id}")
    fun getAlbumTracks(@PathVariable id: String, @RequestParam(required = false) offset: Int?): TrackListResponse
}