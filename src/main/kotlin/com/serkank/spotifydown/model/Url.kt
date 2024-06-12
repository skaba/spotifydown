package com.serkank.spotifydown.model

import com.serkank.spotifydown.ALL_URL_PATTERN

data class Url(val type: Type, val id: String) {
    companion object {
        operator fun invoke(url: String): Url {
            val matchResult = ALL_URL_PATTERN.toRegex().find(url)
            val type = enumValueOf<Type>(matchResult?.groupValues?.get(1).toString().uppercase())
            val id = matchResult?.groupValues?.get(2)!!
            return Url(type, id)
        }
    }
}
