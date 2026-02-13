package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Url
import com.serkank.spotifydown.toFlux
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.core.io.ClassPathResource
import reactor.test.StepVerifier
import java.util.UUID
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
class FileResolverTest {
    @MockK
    private lateinit var compositeResolver: CompositeResolver

    @InjectMockKs
    private lateinit var fileResolver: FileResolver

    @BeforeEach
    fun setUp() {
        every { compositeResolver.resolveTracks(any()) } answers { Track(UUID.randomUUID().toString()).toFlux() }
    }

    @Test
    fun testAllValid() {
        StepVerifier
            .create(fileResolver.resolveTracks(ClassPathResource("valid.txt").file.path))
            .expectNextCount(4)
            .verifyComplete()
        verify {
            compositeResolver.resolveTracks(Url("https://open.spotify.com/album/4oSd9V0mXSw6fVS0MRU91L?si=CY-AdITIQn6AhtA3GinL6g"))
            compositeResolver.resolveTracks(Url("https://open.spotify.com/track/49fzPkBb3aOUWYRKaTWVhm?si=a012a32d42084c78"))
            compositeResolver.resolveTracks(Url("https://open.spotify.com/track/7fC9Hp9RFSDNpgRnSY2Ahj?si=444f4e86f51f469d"))
            compositeResolver.resolveTracks(Url("https://open.spotify.com/track/3GeiSIP7JkXIoMIGrc0hv1?si=844837ebcc034d37"))
        }
    }

    @Test
    fun testInvalid() {
        StepVerifier
            .create(fileResolver.resolveTracks(ClassPathResource("invalid.txt").file.path))
            .verifyError()
        verify(exactly = 0) { compositeResolver.resolveTracks(any()) }
    }
}
