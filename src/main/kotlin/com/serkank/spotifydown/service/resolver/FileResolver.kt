package com.serkank.spotifydown.service.resolver

import com.serkank.spotifydown.model.Track
import com.serkank.spotifydown.model.Type
import com.serkank.spotifydown.model.Type.FILE
import org.springframework.stereotype.Service
import java.io.File

@Service
class FileResolver : Resolver {
    override fun getType(): Type {
        return FILE
    }

    override fun resolveTracks(id: String): List<Track> {
        val file = File(id)
        val tracks = file
            .readLines()
            .stream()
            .map(String::trim)
            .filter(String::isNotBlank)
            .map { idFromFile -> Track(idFromFile) }
            .toList()

        return tracks
    }
}