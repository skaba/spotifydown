package com.serkank.spotifydown

import com.ninjasquad.springmockk.MockkBean
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import xyz.gianlu.librespot.core.Session

@ActiveProfiles("test")
@SpringBootTest
class SpotifyDownTests {
    @MockkBean
    lateinit var session: Session

    @Test
    fun contextLoads() {
    }
}
