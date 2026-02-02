package com.serkank.spotifydown

import com.serkank.spotifydown.shell.Commands
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.shell.core.command.annotation.EnableCommand
import xyz.gianlu.librespot.core.Session
import java.io.File

@Configuration
@EnableCommand(Commands::class)
class Config {
    @Bean(destroyMethod = "close")
    fun session(): Session {
        // val credentialsFile =
        // credentialsFile.createNewFile()
        val conf =
            Session.Configuration
                .Builder()
                .setStoreCredentials(true)
                .setStoredCredentialsFile(File(System.getProperty("user.home"), ".spotify_down"))
                .setCacheEnabled(false)
                /*.setStoreCredentials(true)
                .setStoredCredentialsFile()
                .setTimeSynchronizationMethod()
                .setTimeManualCorrection()
                .setProxyEnabled()
                .setProxyType()
                .setProxyAddress()
                .setProxyPort()
                .setProxyAuth()
                .setProxyUsername()
                .setProxyPassword()
                .setRetryOnChunkError()                 */
                .build()
        return Session
            .Builder(conf)
            .oauth()
            .create()
    }
}
