package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.service.resolver.CompositeResolver
import java.io.File

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
const val HEADER = "https://spotifydown.com"
private val MISSING_FILE = File("missing.txt")

fun logMissing(track: Track) {
    MISSING_FILE.appendText("https://open.spotify.com/track/${track.id}${System.lineSeparator()}")
}

fun Sequence<String>.mapToTracks(compositeResolver: CompositeResolver): Sequence<Track> {
    return this
        .flatMap { compositeResolver.resolveTracks(Url(it)) }
        .distinct()
}

