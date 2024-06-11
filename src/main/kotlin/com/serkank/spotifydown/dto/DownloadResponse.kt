package com.serkank.spotifydown.dto
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class DownloadResponse(
    val success: Boolean,
    val metadata: Metadata,
    val link: String,
)
