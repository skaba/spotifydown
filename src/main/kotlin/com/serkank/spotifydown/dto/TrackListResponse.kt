package com.serkank.spotifydown.dto

import kotlinx.serialization.Serializable

@Serializable
data class TrackListResponse(
    val success: Boolean,
    val nextOffset: Int?,
    val trackList: List<TrackList>,
)