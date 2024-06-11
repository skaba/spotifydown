package com.serkank.spotifydown

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.CommandScan

@SpringBootApplication
@CommandScan
class SpotifyDown

fun main(args: Array<String>) {
    runApplication<SpotifyDown>(*args)
}
