package com.serkank.spotifydown.dto
import kotlinx.serialization.Serializable

@Serializable
data class DownloadResponse(
    val success: Boolean,
    val metadata: Metadata,
    val link: String,
)
