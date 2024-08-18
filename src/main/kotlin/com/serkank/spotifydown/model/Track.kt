package com.serkank.spotifydown.model

data class Track(
    val id: String,
)

fun Track.url(): String = "https://open.spotify.com/track/${this.id}"
