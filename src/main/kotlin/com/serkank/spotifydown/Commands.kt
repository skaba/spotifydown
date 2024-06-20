package com.serkank.spotifydown

import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.service.resolver.FileResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.shell.command.CommandRegistration.OptionArity.ONE_OR_MORE
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.io.File

private val logger = KotlinLogging.logger {}

@Command
class Commands(
    private val compositeResolver: CompositeResolver,
    private val fileResolver: FileResolver,
    private val trackDownloaderService: TrackDownloaderService
) {

    @Command
    fun download(
        @Option(longNames = ["url"], arity = ONE_OR_MORE) urls:
        @Size(min = 1)
        List<@Pattern(regexp = SPOTIFY_URL_PATTERN, message = "Not a valid Spotify URL") String>,
        @Option(longNames = ["dry-run"]) dryRun: Boolean = false
    ) {
        logger.info { "Downloading ${urls.joinToString()}" }

        val tracks = urls
            .asSequence()
            .mapToTracks(compositeResolver)
        trackDownloaderService.download(tracks, dryRun)
    }

    @Command
    fun downloadFile(filename: String, deleteAfter: Boolean = false) {
        logger.info { "Downloading from $filename" }
        val tracks = fileResolver.resolveTracks(filename)
        if (deleteAfter) {
            File(filename).delete()
        }
        trackDownloaderService.download(tracks, false)
    }
}