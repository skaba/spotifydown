package com.serkank.spotifydown

import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.FILE
import com.serkank.spotifydown.service.TrackDownloaderService
import com.serkank.spotifydown.service.resolver.Resolver
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import org.springframework.shell.command.annotation.Command
import org.springframework.shell.command.annotation.Option
import java.io.File

private val logger = KotlinLogging.logger {}

@Command
class Commands(private val resolvers: List<Resolver>, private val trackDownloaderService: TrackDownloaderService) {

    @Command
    fun download(
        @Pattern(regexp = ALL_URL_PATTERN, message = "Not a valid Spotify URL") url: String,
        @Option(longNames = ["dry-run"]) dryRun: Boolean = false
    ) {
        logger.info { "Downloading $url" }
        val matchResult = ALL_URL_PATTERN.toRegex().find(url)
        val type = enumValueOf<Type>(matchResult?.groupValues?.get(1).toString().uppercase())
        val id = matchResult?.groupValues?.get(2)!!

        val tracks = resolvers.find { resolver -> type == resolver.getType() }!!.resolveTracks(id)
        trackDownloaderService.download(tracks, dryRun)
    }

    @Command
    fun downloadFile(filename: String, deleteAfter: Boolean = false) {
        logger.info { "Downloading from $filename" }
        val tracks = resolvers.find { resolver -> resolver.getType() == FILE }!!.resolveTracks(filename)
        if (deleteAfter) {
            File(filename).delete()
        }
        trackDownloaderService.download(tracks, false)
    }
}