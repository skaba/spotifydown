package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.junit.jupiter.api.extension.ExtendWith
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test
import kotlin.test.Test

@ExtendWith(MockKExtension::class)
abstract class ContainerResolverTest<T : ContainerResolver> {
    private val id: String = "ID"

    @InjectMockKs
    lateinit var containerResolver: T

    @MockK
    lateinit var returnValue1: TrackListResponse

    @MockK
    lateinit var returnValue2: TrackListResponse

    @MockK
    lateinit var spotifyDownService: SpotifyDownService

    private fun prepareMocks(noNextOffset: Int?) {
        every { returnValue1.nextOffset } returns 101
        every { returnValue1.trackList } returns (1..100).map(Int::toString).map(::TrackList)

        every { returnValue2.nextOffset } returns noNextOffset
        every { returnValue2.trackList } returns (100..150).map(Int::toString).map(::TrackList)

        every { spotifyDownService.getTracks(type, id, null) } returns returnValue1.toMono()
        every { spotifyDownService.getTracks(type, id, 101) } returns returnValue2.toMono()
    }

    @Test
    fun testResolveTracksWithNullOffset() {
        prepareMocks(null)

        containerResolver
            .resolveTracks(id)
            .test()
            .expectNextCount(150)
            .verifyComplete()

        verifySequence {
            spotifyDownService.getTracks(type, id, null)
            spotifyDownService.getTracks(type, id, 101)
        }
    }

    @Test
    fun testResolveTracksWithZeroOffset() {
        prepareMocks(0)

        containerResolver
            .resolveTracks(id)
            .test()
            .expectNextCount(150)
            .verifyComplete()

        verifySequence {
            spotifyDownService.getTracks(type, id, null)
            spotifyDownService.getTracks(type, id, 101)
        }
    }

    abstract val type: String
}
