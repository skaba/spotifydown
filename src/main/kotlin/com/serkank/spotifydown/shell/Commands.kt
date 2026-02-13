package com.serkank.spotifydown.shell

import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.validator.ValidSpotifyUrl
import com.serkank.spotifydown.writeToFile
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Size
import org.springframework.shell.core.command.annotation.Command
import org.springframework.shell.core.command.annotation.Option
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toFlux
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

@Component
class Commands(
    private val compositeResolver: CompositeResolver,
    private val trackDownloaderService: TrackDownloaderService,
) {
    @Command
    fun download(
        @Option(longName = "url", shortName = 'u', required = true)
        @Size(min = 1)
        urls: List<@ValidSpotifyUrl String>,
        @Option(longName = "dry-run", shortName = 'd', defaultValue = "false") dryRun: Boolean,
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
        @Option(longName = "url", shortName = 'u', required = true)
        @Size(min = 1)
        urls: List<@ValidSpotifyUrl String>,
        @Option(longName = "file", shortName = 'f', required = true) filename: String,
    ) {
        logger.info { "Dumping tracks ${urls.joinToString()} to $filename" }
        val file = Path.of(filename)
        urls
            .toFlux()
            .mapToTracks(compositeResolver)
            .map { "${it.url}" }
            .writeToFile(file)
            .block()
    }
}
