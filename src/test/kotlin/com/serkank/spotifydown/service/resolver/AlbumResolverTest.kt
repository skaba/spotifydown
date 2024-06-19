package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.dto.TrackList
import com.serkank.spotifydown.dto.TrackListResponse
import com.serkank.spotifydown.service.SpotifyDownService
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class AlbumResolverTest {

    private val id: String = "ID"
    private val returnValue1 = mock<TrackListResponse>() {
        on { nextOffset } doReturn 101
        on { trackList } doReturn (1..100).map(Int::toString).map { TrackList(it) }.toList()
    }

    private val returnValue2 = mock<TrackListResponse>() {
        on { trackList } doReturn (101..150).map(Int::toString).map { TrackList(it) }.toList()
    }

    private val spotifyDownService = mock<SpotifyDownService>() {
        on { getTracks("album", id, null) } doReturn returnValue1
        on { getTracks("album", id, 101) } doReturn returnValue2
    }

    private val albumResolver = AlbumResolver(spotifyDownService)

    @Test
    fun testResolveTracks() {
        val tracks = albumResolver.resolveTracks(id).toMutableList()
        verify(spotifyDownService).getTracks("album", id, null)
        verify(spotifyDownService).getTracks("album", id, 101)
        assertEquals(150, tracks.size)
    }
}