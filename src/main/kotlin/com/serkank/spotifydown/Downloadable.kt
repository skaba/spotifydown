package com.serkank.spotifydown

import org.springframework.web.client.RestClient

abstract class Downloadable(val id: String, val restClientBuilder: RestClient.Builder) {
    abstract fun download()
}