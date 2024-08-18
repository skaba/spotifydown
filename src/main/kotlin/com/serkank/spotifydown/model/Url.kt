package com.serkank.spotifydown.model

import com.serkank.spotifydown.SPOTIFY_URL_PATTERN

data class Url(
    val type: Type,
    val id: String,
) {
    companion object {
        operator fun invoke(url: String): Url {
            val regex = SPOTIFY_URL_PATTERN.toRegex()
            val matchResult = regex.find(url)
            val type =
                enumValueOf<Type>(requireNotNull(matchResult) { "Invalid Spotify URL: $url" }.groupValues[1].uppercase())
            val id = matchResult.groupValues[2]
            return Url(type, id)
        }
    }
}
