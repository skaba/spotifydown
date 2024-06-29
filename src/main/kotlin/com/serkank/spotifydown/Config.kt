package com.serkank.spotifydown

import com.serkank.spotifydown.service.SpotifyDownService
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.shell.command.annotation.CommandScan
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory


@Configuration
@CommandScan
class Config {

    @Bean
    fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        return KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = true
        })
    }

    @Bean
    fun spotifyDownService(restClientBuilder: RestClient.Builder): SpotifyDownService {
        val restClient = restClientBuilder
            .defaultHeader("REFERER", HEADER)
            .defaultHeader("ORIGIN", HEADER)
            .baseUrl("https://api.spotifydown.com/")
            .build()
        val adapter = RestClientAdapter
            .create(restClient)
        val factory = HttpServiceProxyFactory
            .builderFor(adapter)
            .build()
        return factory
            .createClient(SpotifyDownService::class.java)
    }
}