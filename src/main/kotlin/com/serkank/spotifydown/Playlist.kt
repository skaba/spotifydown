package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import kotlinx.serialization.json.Json

class Playlist(id: String) : Container(id) {
    override fun getTracks(offset: Int?): TrackListResponse {

        val response = request("https://api.spotifydown.com/trackList/playlist/$id${offset(offset)}")
            .execute()
            .returnContent()
            .toString()
        val trackListResponse = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<TrackListResponse>(response)
        return trackListResponse;
    }
}