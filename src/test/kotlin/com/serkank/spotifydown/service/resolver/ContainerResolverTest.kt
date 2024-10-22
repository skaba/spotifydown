package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import reactor.core.publisher.Mono.just
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class ContainerResolverTest<T : ContainerResolver>(
    private val constructor: (SpotifyDownService) -> T,
) {
    private val id: String = "ID"

    private fun prepareMocks(noNextOffset: Int?): Pair<T, SpotifyDownService> {
        val returnValue1 =
            mock<TrackListResponse> {
                on { nextOffset } doReturn 101
                on { trackList } doReturn (1..100).map(Int::toString).map(::TrackList)
            }
        val returnValue2 =
            mock<TrackListResponse> {
                on { nextOffset } doReturn noNextOffset
                on { trackList } doReturn (100..150).map(Int::toString).map(::TrackList)
            }

        val spotifyDownService =
            mock<SpotifyDownService> {
                on { getTracks(type, id, null) } doReturn just(returnValue1)
                on { getTracks(type, id, 101) } doReturn just(returnValue2)
            }
        val containerResolver = constructor.invoke(spotifyDownService)
        return containerResolver to spotifyDownService
    }

    @Test
    fun testResolveTracksWithNullOffset() {
        val (containerResolver, spotifyDownService) = prepareMocks(null)
        val tracks =
            containerResolver
                .resolveTracks(id)
                .collectList()
                .block()
        verify(spotifyDownService).getTracks(type, id, null)
        verify(spotifyDownService).getTracks(type, id, 101)
        assertEquals(150, tracks!!.size)
    }

    @Test
    fun testResolveTracksWithZeroOffset() {
        val (containerResolver, spotifyDownService) = prepareMocks(0)
        val tracks =
            containerResolver
                .resolveTracks(id)
                .collectList()
                .block()
        verify(spotifyDownService).getTracks(type, id, null)
        verify(spotifyDownService).getTracks(type, id, 101)
        assertEquals(150, tracks!!.size)
    }

    abstract val type: String
}
