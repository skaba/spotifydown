package com.serkank.spotifydown

import com.serkank.spotifydown.service.SpotifyDownService
import kotlinx.serialization.json.Json
import org.springframework.boot.web.codec.CodecCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.shell.command.annotation.CommandScan
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

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

    @Bean
    fun spotifyDownService(webClientBuilder: WebClient.Builder): SpotifyDownService {
        val webClient =
            webClientBuilder
                .defaultHeader("REFERER", HEADER)
                .defaultHeader("ORIGIN", HEADER)
                .baseUrl("https://api.spotifydown.com/")
                .build()
        val adapter =
            WebClientAdapter
                .create(webClient)
        val factory =
            HttpServiceProxyFactory
                .builderFor(adapter)
                .build()
        return factory
            .createClient(SpotifyDownService::class.java)
    }
}
