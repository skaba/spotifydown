package com.serkank.spotifydown

import com.fasterxml.jackson.databind.DeserializationFeature
import kotlinx.serialization.json.Json
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer
import org.springframework.boot.web.client.RestClientCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.apache.hc.core5.http.HttpHeaders.REFERER
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
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