package com.serkank.spotifydown

import kotlinx.serialization.json.Json
import org.apache.hc.core5.http.HttpHeaders.REFERER
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class Config {
    @Bean
    fun addDefaultHeaders(): RestClientCustomizer {
        return RestClientCustomizer { restClient ->
            restClient
                .defaultHeader(REFERER, HEADER)
                .defaultHeader("ORIGIN", HEADER)
        }
    }

    @Bean
    fun messageConverter(): KotlinSerializationJsonHttpMessageConverter {
        return KotlinSerializationJsonHttpMessageConverter(Json {
            ignoreUnknownKeys = true
        })
    }
}