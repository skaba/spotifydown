package com.serkank.spotifydown.model

data class Track(
    val id: String,
) {
    val url = Url(Type.TRACK, id)
}
