package com.serkank.spotifydown.model

import com.serkank.spotifydown.model.Type.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UrlTest {
    @Test
    fun testFromYahooUrl() {
        assertFailsWith<IllegalArgumentException>("Invalid Spotify URL: http://www.yahoo.com") { Url("http://www.yahoo.com") }
    }

    @Test
    fun testFromTrackUrl() {
        val (type, id) = Url("https://open.spotify.com/track/0DVsDOAD6fjZ5czDwa68tn?si=47ba33c253a54bb9")
        assertEquals(TRACK, type)
        assertEquals("0DVsDOAD6fjZ5czDwa68tn", id)
    }

    @Test
    fun testFromPlaylistUrl() {
        val (type, id) = Url("https://open.spotify.com/playlist/37i9dQZF1DZ06evO1q6zh6?si=70b6db5dc3414824")
        assertEquals(PLAYLIST, type)
        assertEquals("37i9dQZF1DZ06evO1q6zh6", id)
    }

    @Test
    fun testFromAlbumUrl() {
        val (type, id) = Url("https://open.spotify.com/album/7LgrhuKnAXpNEv8qzcVd2t?si=pj4kZC1FSVa8-QCTiQP2hQ")
        assertEquals(ALBUM, type)
        assertEquals("7LgrhuKnAXpNEv8qzcVd2t", id)
    }
}