package com.serkank.spotifydown.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.serialization.Serializable

@Serializable
@JsonIgnoreProperties(ignoreUnknown = true)
data class Metadata(
    val success: Boolean,
    val id: String,
    val artists: String,
    val title: String,
)
