package com.serkank.spotifydown

import kotlinx.serialization.json.Json
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.shell.command.annotation.CommandScan
import xyz.gianlu.librespot.core.Session
import java.io.File

@Configuration
@CommandScan
class Config {
    @Bean
    fun codecCustomizer(): CodecCustomizer =
        CodecCustomizer {
            it.defaultCodecs().kotlinSerializationJsonDecoder(
                KotlinSerializationJsonDecoder(
                    Json {
                        ignoreUnknownKeys = true
                    },
                ),
            )
        }

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
