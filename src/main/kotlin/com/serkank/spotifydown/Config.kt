package com.serkank.spotifydown

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import xyz.gianlu.librespot.core.Session
import java.io.File

@Configuration
class Config {
    @Bean
    fun session(): Session {
        val conf =
            Session.Configuration
                .Builder()
                .setStoreCredentials(true)
                .setStoredCredentialsFile(File(System.getProperty("user.home"), ".spotify_down"))
                .setCacheEnabled(false)
                .build()
        return Session
            .Builder(conf)
            .oauth()
            .create()
    }
}
