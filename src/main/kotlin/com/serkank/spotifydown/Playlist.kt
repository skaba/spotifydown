package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import kotlinx.serialization.json.Json
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

class Playlist(id: String, restClientBuilder: RestClient.Builder) : Container(id, restClientBuilder) {
    override fun getTracks(offset: Int?): TrackListResponse {

        return restClientBuilder
            .build()
            .get()
            .uri("https://api.spotifydown.com/trackList/playlist/$id${offset(offset)}")
            .retrieve()
            .toEntity<TrackListResponse>()
            .body!!

/*        val response = request("https://api.spotifydown.com/trackList/playlist/$id${offset(offset)}")
            .execute()
            .returnContent()
            .toString()
        val trackListResponse = Json {
            ignoreUnknownKeys = true
        }.decodeFromString<TrackListResponse>(response)
        return trackListResponse;*/
    }
}