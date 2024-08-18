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
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE
import kotlin.io.path.appendText

const val SPOTIFY_URL_PATTERN = """https?:\/\/[^/]*open\.spotify\.com\/(track|playlist|album)\/([^\s?]+)(\?.*)?"""
const val HEADER = "https://spotifydown.com"
private val MISSING_FILE = Path.of("missing.txt")

fun logMissing(track: Track): Mono<Void> = appendTrackUrl(track, MISSING_FILE)

fun Flux<String>.mapToTracks(compositeResolver: CompositeResolver): Flux<Track> =
    this
        .flatMap {
            compositeResolver.resolveTracks(Url(it))
        }.distinct()

fun Flux<String>.writeToFile(path: Path): Mono<Void> =
    writeRows(
        this,
        path,
        CREATE_NEW,
        WRITE,
    )

fun appendTrackUrl(
    track: Track,
    path: Path,
): Mono<Void> = { path.appendText(track.url()) }.toMono().then()

fun writeRows(
    rowsFlux: Flux<String>,
    path: Path,
    vararg options: StandardOpenOption,
): Mono<Void> {
    val bufferFactory = DefaultDataBufferFactory()
    val encoder = CharSequenceEncoder.textPlainOnly()

    val dataBufferFlux =
        rowsFlux
            .map { it + System.lineSeparator() }
            .map { encoder.encodeValue(it, bufferFactory, ResolvableType.NONE, null, null) }

    return DataBufferUtils.write(
        dataBufferFlux,
        path,
        *options,
    )
}
