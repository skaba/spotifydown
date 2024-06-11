package com.serkank.spotifydown

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import org.springframework.shell.command.annotation.Command

private val logger = KotlinLogging.logger {}

@Command
class Commands {

    @Command
    fun download(@Pattern(regexp = ALL_URL_PATTERN, message = "Not a valid Spotify URL") url: String) {
        logger.info {"Downloading $url" }
        val matchResult = ALL_URL_PATTERN.toRegex().find(url)
        val type = enumValueOf<Type>(matchResult?.groupValues?.get(1).toString().uppercase())
        val id = matchResult?.groupValues?.get(2)
        when(type) {
            Type.TRACK -> Track(id!!).download()
            Type.ALBUM -> TODO()
            Type.PLAYLIST -> Playlist(id!!).download()
        }
    }
}