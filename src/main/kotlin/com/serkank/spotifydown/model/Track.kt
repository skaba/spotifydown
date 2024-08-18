package com.serkank.spotifydown.model

data class Track(
    val id: String,
) {
    fun url(): String = "https://open.spotify.com/track/${this.id}"
}
