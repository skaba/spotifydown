package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.service.resolver.CompositeResolver
import java.io.File

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
val SPOTIFY_URL_REGEX = SPOTIFY_URL_PATTERN.toRegex()
const val HEADER = "https://spotifydown.com"

fun Sequence<String>.mapToTracks(compositeResolver: CompositeResolver): Sequence<Track> =
    this
        .flatMap { compositeResolver.resolveTracks(Url(it)) }
        .distinct()

private val MISSING_FILE = File("missing.txt")

fun logMissing(track: Track) {
    appendTrackUrl(track, MISSING_FILE)
}

fun appendTrackUrl(
    track: Track,
    file: File,
) {
    file.appendText("${track.url}${System.getProperty("line.separator")}")
}
