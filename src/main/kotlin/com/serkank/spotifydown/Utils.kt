package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.service.resolver.CompositeResolver
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.kotlin.core.publisher.toMono
import java.io.File

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
val SPOTIFY_URL_REGEX = SPOTIFY_URL_PATTERN.toRegex()
const val HEADER = "https://spotifydown.com"
private val MISSING_FILE = File("missing.txt")

fun logMissing(track: Track): Mono<Track> =
    { MISSING_FILE.appendText(track.url() + System.lineSeparator()) }.toMono().then(
        empty(),
    )

fun Sequence<String>.mapToTracks(compositeResolver: CompositeResolver): Sequence<Track> =
    this
        .flatMap { compositeResolver.resolveTracks(Url(it)) }
        .distinct()
