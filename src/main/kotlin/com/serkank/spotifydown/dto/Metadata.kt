package com.serkank.spotifydown.dto
import kotlinx.serialization.Serializable

@Serializable
data class Metadata(
    val success: Boolean,
    val id: String,
    val artists: String,
    val title: String,
    val album: String,
    val cover: String,
    val isrc: String,
    val releaseDate: String,
)
