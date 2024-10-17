package com.serkank.spotifydown.shell

import com.serkank.spotifydown.appendTrackUrl
import com.serkank.spotifydown.mapToTracks
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.service.resolver.FileResolver
import com.serkank.spotifydown.validator.ValidSpotifyUrl
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Size
import org.springframework.shell.command.CommandRegistration
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.io.File
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

@Command
class Commands(
    private val compositeResolver: CompositeResolver,
    private val fileResolver: FileResolver,
    private val trackDownloaderService: TrackDownloaderService,
) {
    @Command
    fun download(
        @Option(longNames = ["url"], shortNames = ['u'], arity = CommandRegistration.OptionArity.ONE_OR_MORE)
        @Size(min = 1)
        @ValidSpotifyUrl
        urls: List<String>,
        @Option(longNames = ["dry-run"], shortNames = ['d']) dryRun: Boolean = false,
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
        @Option(longNames = ["url"], shortNames = ['u'], arity = CommandRegistration.OptionArity.ONE_OR_MORE)
        @Size(min = 1)
        @ValidSpotifyUrl
        urls: List<String>,
        @Option(longNames = ["file"], shortNames = ['f']) filename: String,
    ) {
        logger.info { "Dumping tracks ${urls.joinToString()} to $filename" }
        val file = Path.of(filename)
        urls
            .asSequence()
            .mapToTracks(compositeResolver)
            .forEach { appendTrackUrl(it, file) }
    }

    @Command
    fun downloadFile(
        @Option(longNames = ["file"], shortNames = ['f']) filename: String,
        @Option(longNames = ["delete-after"]) deleteAfter: Boolean = false,
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
