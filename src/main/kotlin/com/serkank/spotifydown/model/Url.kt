package com.serkank.spotifydown.model

import com.serkank.spotifydown.SPOTIFY_URL_REGEX

data class Url(
    val type: Type,
    val id: String,
) {
    companion object {
        operator fun invoke(url: String): Url {
            val matchResult = SPOTIFY_URL_REGEX.find(url)
            val type =
                enumValueOf<Type>(requireNotNull(matchResult) { "Invalid Spotify URL: $url" }.groupValues[1].uppercase())
            val id = matchResult.groupValues[2]
            return Url(type, id)
        }
    }

    override fun toString(): String = "https://open.spotify.com/${type.name.lowercase()}/${this.id}"
}
