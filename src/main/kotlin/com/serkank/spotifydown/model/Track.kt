package com.serkank.spotifydown.model

data class Track(
    val id: String,
) {
    val url = "https://open.spotify.com/track/${this.id}"
}
