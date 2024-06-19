package com.serkank.spotifydown.model

import java.util.Locale.ROOT

enum class Type {
    TRACK,
    ALBUM,
    PLAYLIST,
    FILE;

    override fun toString(): String {
        return super.toString().lowercase(ROOT)
    }
}