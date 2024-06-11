package com.serkank.spotifydown

import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.HttpHeaders.REFERER
import org.springframework.web.client.RestClient

abstract class Downloadable(val id: String, val restClientBuilder: RestClient.Builder) {
    abstract fun download()



        fun request(url: String): Request {
            return Request
                .get(url)
                .addHeader(REFERER, HEADER)
                .addHeader("ORIGIN", HEADER)
        }

}