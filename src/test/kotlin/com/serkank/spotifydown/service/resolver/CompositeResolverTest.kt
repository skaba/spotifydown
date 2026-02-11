package com.serkank.spotifydown.service.resolver

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type.ALBUM
import com.serkank.spotifydown.model.Type.FILE
import com.serkank.spotifydown.model.Type.PLAYLIST
import com.serkank.spotifydown.model.Type.TRACK
import com.serkank.spotifydown.model.Url
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Flux.empty
import xyz.gianlu.librespot.core.Session
import kotlin.test.Test
import kotlin.test.assertSame

@SpringBootTest
class CompositeResolverTest {
    val id: String = "ID"
    val returnValue = empty<Track>()

    @MockkBean
    lateinit var session: Session

    @SpykBean
    lateinit var playlistResolver: PlaylistResolver

    @SpykBean
    lateinit var albumResolver: AlbumResolver

    @SpykBean
    lateinit var trackResolver: TrackResolver

    @SpykBean
    lateinit var fileResolver: FileResolver

    @Autowired
    lateinit var compositeResolver: CompositeResolver

    @Test
    fun testPlaylistResolver() {
        every { playlistResolver.resolveTracks(any()) } returns returnValue
        assertSame(returnValue, compositeResolver.resolveTracks(Url(PLAYLIST, id)))
    }

    @Test
    fun testAlbumResolver() {
        every { albumResolver.resolveTracks(any()) } returns returnValue
        assertSame(returnValue, compositeResolver.resolveTracks(Url(ALBUM, id)))
    }

    @Test
    fun testTrackResolver() {
        every { trackResolver.resolveTracks(any()) } returns returnValue
        assertSame(returnValue, compositeResolver.resolveTracks(Url(TRACK, id)))
    }

    @Test
    fun testFileResolver() {
        every { fileResolver.resolveTracks(any()) } returns returnValue
        assertSame(returnValue, compositeResolver.resolveTracks(Url(FILE, id)))
    }
}
