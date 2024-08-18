package com.serkank.spotifydown

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.url
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.CompositeResolver
import com.serkank.spotifydown.service.resolver.FileResolver
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.shell.command.CommandRegistration.OptionArity.ONE_OR_MORE
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import reactor.core.publisher.Flux.fromIterable
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
        @Option(longNames = ["url"], shortNames = ['u'], arity = ONE_OR_MORE) urls:
            @Size(min = 1)
            List<
                @Pattern(regexp = SPOTIFY_URL_PATTERN, message = "Not a valid Spotify URL")
                String,
            >,
        @Option(longNames = ["dry-run"]) dryRun: Boolean = false,
    ) {
        logger.info { "Downloading ${urls.joinToString()}" }
        val tracks =
            fromIterable(urls)
                .mapToTracks(compositeResolver)
        trackDownloaderService.download(tracks, dryRun).then().block()
    }

    @Command
    fun dump(
        @Option(longNames = ["url"], shortNames = ['u'], arity = ONE_OR_MORE) urls:
            @Size(min = 1)
            List<
                @Pattern(regexp = SPOTIFY_URL_PATTERN, message = "Not a valid Spotify URL")
                String,
            >,
        @Option(longNames = ["file"], shortNames = ['f']) filename: String,
    ) {
        logger.info { "Dumping tracks ${urls.joinToString()} to $filename" }
        val file = Path.of(filename)
        fromIterable(urls)
            .mapToTracks(compositeResolver)
            .map(Track::url)
            .writeToFile(file)
            .block()
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
        trackDownloaderService.download(tracks, false).then().block()
    }
}
