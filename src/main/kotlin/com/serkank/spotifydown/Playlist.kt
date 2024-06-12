package com.serkank.spotifydown

import org.springframework.web.client.RestClient

class Playlist(id: String, restClientBuilder: RestClient.Builder) : UrlTracks(id, restClientBuilder) {
    override fun getUrl(offset: Int?): String {
        return "https://api.spotifydown.com/trackList/playlist/$id${offset(offset)}"
    }
}