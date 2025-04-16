package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.spotify.metadata.Metadata
import xyz.gianlu.librespot.common.Utils.bytesToHex
import xyz.gianlu.librespot.metadata.TrackId
import java.io.File

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
val SPOTIFY_URL_REGEX = SPOTIFY_URL_PATTERN.toRegex()
private val MISSING_FILE = File("missing.txt")

fun logMissing(track: Track) {
    appendTrackUrl(track, MISSING_FILE)
}

fun Sequence<String>.mapToTracks(compositeResolver: CompositeResolver): Sequence<Track> =
    this
        .flatMap { compositeResolver.resolveTracks(Url(it)) }
        .distinct()

fun appendTrackUrl(
    track: Track,
    file: File,
) {
    file.appendText("${track.url}${System.getProperty("line.separator")}")
}

val Metadata.Track.uri: String
    get() = TrackId.fromHex(bytesToHex(this.gid.toByteArray())).toSpotifyUri()

val Metadata.Track.id get() =
    this.uri
        .split(':')
        .last()

val Metadata.Track.url get() = "https://open.spotify.com/track/${this.id}"
