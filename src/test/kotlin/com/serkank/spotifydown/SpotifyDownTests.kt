package com.serkank.spotifydown

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import xyz.gianlu.librespot.core.Session

@SpringBootTest
class SpotifyDownTests {
    @MockkBean
    lateinit var session: Session

    @Test
    fun contextLoads() {
    }
}
