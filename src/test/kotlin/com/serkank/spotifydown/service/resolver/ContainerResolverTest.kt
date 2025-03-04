package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verifySequence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.extension.ExtendWith
import reactor.core.publisher.Mono.just
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

        every { spotifyDownService.getTracks(type, id, null) } returns just(returnValue1)
        every { spotifyDownService.getTracks(type, id, 101) } returns just(returnValue2)
    }

    @Test
    fun testResolveTracksWithNullOffset() {
        prepareMocks(null)
        val tracks =
            containerResolver
                .resolveTracks(id)
                .collectList()
                .block()
        verifySequence {
            spotifyDownService.getTracks(type, id, null)
            spotifyDownService.getTracks(type, id, 101)
        }
        assertThat(tracks).hasSize(150)
    }

    @Test
    fun testResolveTracksWithZeroOffset() {
        prepareMocks(0)
        val tracks =
            containerResolver
                .resolveTracks(id)
                .collectList()
                .block()
        verifySequence {
            spotifyDownService.getTracks(type, id, null)
            spotifyDownService.getTracks(type, id, 101)
        }
        assertThat(tracks).hasSize(150)
    }

    abstract val type: String
}
