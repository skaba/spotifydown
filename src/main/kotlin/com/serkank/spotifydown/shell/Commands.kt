package com.serkank.spotifydown.shell

import com.serkank.spotifydown.appendTrackUrl
import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.service.resolver.FileResolver
import com.serkank.spotifydown.validator.ValidSpotifyUrl
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Size
import org.springframework.shell.core.command.annotation.Command
import org.springframework.shell.core.command.annotation.Option
import org.springframework.stereotype.Component
import java.io.File

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
        @Option(longName = "dry-run", shortName = 'd', required = false, defaultValue = "false") dryRun: Boolean,
    ) {
        logger.info { "Downloading ${urls.joinToString()}" }
        val tracks =
            urls
                .asSequence()
                .mapToTracks(compositeResolver)
        val count =
            trackDownloaderService
                .download(tracks, dryRun)
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
        val file = File(filename)
        urls
            .asSequence()
            .mapToTracks(compositeResolver)
            .forEach { appendTrackUrl(it, file) }
    }

    @Command
    fun downloadFile(
        @Option(longName = "file", shortName = 'f') filename: String,
        @Option(longName = "delete-after", required = false, defaultValue = "false") deleteAfter: Boolean,
    ) {
        logger.info { "Downloading from $filename" }
        val tracks = fileResolver.resolveTracks(filename)
        if (deleteAfter) {
            File(filename).delete()
        }
        val count =
            trackDownloaderService
                .download(tracks, false)
        logger.info { "Downloaded $count track(s)" }
    }
}
