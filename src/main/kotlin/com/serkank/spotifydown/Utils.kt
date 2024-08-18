package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.model.url
import com.serkank.spotifydown.service.resolver.CompositeResolver
import org.springframework.core.ResolvableType
import org.springframework.core.codec.CharSequenceEncoder
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
val SPOTIFY_URL_REGEX = SPOTIFY_URL_PATTERN.toRegex()
const val HEADER = "https://spotifydown.com"
private val MISSING_FILE = File("missing.txt")

fun logMissing(track: Track): Mono<Void> = { MISSING_FILE.appendText(track.url() + System.lineSeparator()) }.toMono().then()

fun Flux<String>.mapToTracks(compositeResolver: CompositeResolver): Flux<Track> =
    this
        .flatMap {
            compositeResolver.resolveTracks(Url(it))
        }.distinct()

fun Flux<String>.writeToFile(path: Path): Mono<Void> {
    val bufferFactory = DefaultDataBufferFactory()
    val encoder = CharSequenceEncoder.textPlainOnly()

    val dataBufferFlux =
        this
            .map { it + System.lineSeparator() }
            .map { encoder.encodeValue(it, bufferFactory, ResolvableType.NONE, null, null) }

    return DataBufferUtils.write(
        dataBufferFlux,
        path,
        CREATE_NEW,
        WRITE,
    )
}
