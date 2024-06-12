package com.serkank.spotifydown

import org.springframework.web.client.RestClient

class Album(id: String, restClientBuilder: RestClient.Builder) : UrlTracks(id, restClientBuilder) {
    override fun getUrl(offset: Int?): String {
        return "https://api.spotifydown.com/trackList/album/$id${offset(offset)}"
    }

}