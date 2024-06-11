package com.serkank.spotifydown

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.shell.command.annotation.Command
import org.springframework.web.client.RestClient

private val logger = KotlinLogging.logger {}

@Command
class Commands {

    @Autowired
    lateinit var restClientBuilder: RestClient.Builder

    @Command
    fun download(@Pattern(regexp = ALL_URL_PATTERN, message = "Not a valid Spotify URL") url: String) {
        logger.info {"Downloading $url" }
        val matchResult = ALL_URL_PATTERN.toRegex().find(url)
        val type = enumValueOf<Type>(matchResult?.groupValues?.get(1).toString().uppercase())
        val id = matchResult?.groupValues?.get(2)
        when(type) {
            Type.TRACK -> Track(id!!, restClientBuilder).download()
            Type.ALBUM -> TODO()
            Type.PLAYLIST -> Playlist(id!!, restClientBuilder).download()
        }
        logger.info { "downloaded" }
    }
}