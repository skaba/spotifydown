package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.*
import com.serkank.spotifydown.model.Url
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

@SpringBootTest
class CompositeResolverTest {

    val id: String = "ID"
    val returnValue = emptySequence<Track>()

    @SpyBean
    lateinit var playlistResolver: PlaylistResolver

    @SpyBean
    lateinit var albumResolver: AlbumResolver

    @SpyBean
    lateinit var trackResolver: TrackResolver

    @Autowired
    lateinit var compositeResolver: CompositeResolver

    @Test
    fun testPlaylistResolver() {
        `when`(playlistResolver.resolveTracks(id)).thenReturn(returnValue)
        assertSame(returnValue, compositeResolver.resolveTracks(Url(PLAYLIST, id)))
    }


    @Test
    fun testAlbumResolver() {
        `when`(albumResolver.resolveTracks(id)).thenReturn(returnValue)
        assertSame(returnValue, compositeResolver.resolveTracks(Url(ALBUM, id)))
    }

    @Test
    fun testTrackResolver() {
        `when`(trackResolver.resolveTracks(id)).thenReturn(returnValue)
        assertSame(returnValue, compositeResolver.resolveTracks(Url(TRACK, id)))
    }

    @Test
    fun testFileResolver() {
        assertFailsWith<NullPointerException> { compositeResolver.resolveTracks(Url(FILE, id)) }
    }
}