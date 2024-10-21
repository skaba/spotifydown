package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import reactor.core.publisher.Mono.just
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class ContainerResolverResolverTest<T : ContainerResolverResolver>(
    clazz: KClass<T>,
) {
    private val id: String = "ID"
    private val returnValue1 =
        mock<TrackListResponse> {
            on { nextOffset } doReturn 101
            on { trackList } doReturn (1..100).map(Int::toString).map { TrackList(it) }.toList()
        }

    private val returnValue2 =
        mock<TrackListResponse> {
            on { nextOffset } doReturn null
            on { trackList } doReturn (100..150).map(Int::toString).map { TrackList(it) }.toList()
        }

    private val spotifyDownService =
        mock<SpotifyDownService> {
            on { getTracks(getType(), id, null) } doReturn just(returnValue1)
            on { getTracks(getType(), id, 101) } doReturn just(returnValue2)
        }

    private val urlResolver = clazz.constructors.first().call(spotifyDownService)

    @Test
    fun testResolveTracks() {
        val tracks =
            urlResolver
                .resolveTracks(id)
                .collectList()
                .block()
        verify(spotifyDownService).getTracks(getType(), id, null)
        verify(spotifyDownService).getTracks(getType(), id, 101)
        assertEquals(150, tracks!!.size)
    }

    abstract fun getType(): String
}
