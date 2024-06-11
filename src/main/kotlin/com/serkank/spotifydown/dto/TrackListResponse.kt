package com.serkank.spotifydown.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackListResponse(
    val success: Boolean,
    val nextOffset: String?,
    val trackList: List<TrackList>,
)