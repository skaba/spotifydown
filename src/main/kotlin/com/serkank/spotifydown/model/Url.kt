package com.serkank.spotifydown.model

import com.serkank.spotifydown.FILE_PREFIX
import com.serkank.spotifydown.SPOTIFY_URL_REGEX

data class Url(
    val type: Type,
    val id: String,
) {
    companion object {
        operator fun invoke(url: String): Url =
            if (url.startsWith(FILE_PREFIX)) {
                Url(Type.FILE, url.removePrefix(FILE_PREFIX))
            } else {
                val matchResult = SPOTIFY_URL_REGEX.find(url)
                val type =
                    enumValueOf<Type>(requireNotNull(matchResult) { "Invalid Spotify URL: $url" }.groupValues[1].uppercase())
                val id = matchResult.groupValues[2]
                Url(type, id)
            }
    }

    override fun toString(): String = "https://open.spotify.com/${type.name.lowercase()}/${this.id}"
}
