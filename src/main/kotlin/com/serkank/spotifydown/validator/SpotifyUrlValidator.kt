package com.serkank.spotifydown.validator

import com.serkank.spotifydown.SPOTIFY_URL_REGEX
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.http.HttpHeaders.LOCATION
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.FOUND
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.OK
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.URLEncoder
import kotlin.reflect.KClass

@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.TYPE,
)
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [SpotifyUrlValidator::class])
annotation class ValidSpotifyUrl(
    val message: String = "",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

private const val INVALID_SPOTIFY_URL = "Not a valid Spotify URL"

@Component
class SpotifyUrlValidator(
    restClientBuilder: RestClient.Builder,
) : ConstraintValidator<ValidSpotifyUrl, List<String>> {
    private val restClient =
        restClientBuilder
            .defaultStatusHandler(
                { _: HttpStatusCode? -> true },
                { _: HttpRequest?, _: ClientHttpResponse? -> },
            ).build()

    override fun isValid(
        value: List<String>,
        context: ConstraintValidatorContext,
    ): Boolean {
        var valid = true
        context.disableDefaultConstraintViolation()
        for (url in value) {
            if (!url.matches(SPOTIFY_URL_REGEX)) {
                context.buildConstraintViolationWithTemplate(INVALID_SPOTIFY_URL).addConstraintViolation()
                return false
            }
            val entity =
                restClient
                    .head()
                    .uri(url)
                    .retrieve()
                    .toBodilessEntity()

            val status =
                HttpStatus.valueOf(
                    entity
                        .statusCode
                        .value(),
                )
            when (status) {
                OK -> {
                    // Valid do nothing
                }

                BAD_REQUEST -> {
                    context.buildConstraintViolationWithTemplate(INVALID_SPOTIFY_URL).addConstraintViolation()
                    valid = false
                }

                NOT_FOUND -> {
                    context.buildConstraintViolationWithTemplate("$url not found").addConstraintViolation()
                    valid = false
                }

                FOUND -> {
                    val expectedLocation = "https://accounts.spotify.com/login?continue=${URLEncoder.encode(url, Charsets.UTF_8)}"
                    if (entity.headers.getFirst(LOCATION) != null &&
                        entity.headers
                            .getFirst(LOCATION)!!
                            .startsWith(expectedLocation)
                    ) {
                        context
                            .buildConstraintViolationWithTemplate("$url not accessible, private playlist?")
                            .addConstraintViolation()
                    } else {
                        context
                            .buildConstraintViolationWithTemplate("Error validating $url ($status)")
                            .addConstraintViolation()
                    }
                    valid = false
                }

                else -> {
                    context
                        .buildConstraintViolationWithTemplate("Error validating $url ($status)")
                        .addConstraintViolation()
                    valid = false
                }
            }
        }

        return valid
    }
}
