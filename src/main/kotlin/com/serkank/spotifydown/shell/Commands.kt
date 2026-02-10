package com.serkank.spotifydown.shell

import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.service.resolver.FileResolver
import com.serkank.spotifydown.validator.ValidSpotifyUrl
import com.serkank.spotifydown.writeToFile
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Size
import org.springframework.shell.core.command.annotation.Command
import org.springframework.shell.core.command.annotation.Option
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

@Component
class Commands(
    private val compositeResolver: CompositeResolver,
    private val fileResolver: FileResolver,
    private val trackDownloaderService: TrackDownloaderService,
) {
    @Command
    fun download(
        @Option(longName = "url", shortName = 'u')
        @Size(min = 1)
        urls: List<@ValidSpotifyUrl String>,
        @Option(longName = "dry-run", shortName = 'd') dryRun: Boolean = false,
    ) {
        logger.info { "Downloading ${urls.joinToString()}" }
        val tracks =
            urls
                .toFlux()
                .mapToTracks(compositeResolver)
        val count =
            trackDownloaderService
                .download(tracks, dryRun)
                .block()
        logger.info { "Downloaded $count track(s)" }
    }

    @Command
    fun dump(
        @Option(longName = "url", shortName = 'u')
        @Size(min = 1)
        @ValidSpotifyUrl
        urls: List<String>,
        @Option(longName = "file", shortName = 'f') filename: String,
    ) {
        logger.info { "Dumping tracks ${urls.joinToString()} to $filename" }
        val file = Path.of(filename)
        urls
            .toFlux()
            .mapToTracks(compositeResolver)
            .map { it.url }
            .writeToFile(file)
            .block()
    }

    @Command
    fun downloadFile(
        @Option(longName = "file", shortName = 'f') filename: String,
        @Option(longName = "delete-after") deleteAfter: Boolean = false,
    ) {
        logger.info { "Downloading from $filename" }
        val tracks = fileResolver.resolveTracks(filename)
        if (deleteAfter) {
            File(filename).delete()
        }
        val count =
            trackDownloaderService
                .download(tracks, false)
                .block()
        logger.info { "Downloaded $count track(s)" }
    }
}
