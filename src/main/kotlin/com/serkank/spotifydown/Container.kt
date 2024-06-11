package com.serkank.spotifydown

import com.serkank.spotifydown.dto.TrackListResponse
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.web.client.RestClient

abstract class Container(id: String, restClientBuilder: RestClient.Builder) : Downloadable(id, restClientBuilder) {
    abstract fun getTracks(offset: Int?) : TrackListResponse

    fun resolveTracks(): List<Track> {
        val responses: MutableList<TrackListResponse> = mutableListOf()
        var response = getTracks(null)
        responses.add(response)
        while(response.nextOffset != null) {
            response = getTracks(response.nextOffset?.toInt())
            responses.add(response)
        }

        return responses
            .stream()
            .flatMap { r -> r.trackList.stream() }
            .map {  t -> Track(t.id, restClientBuilder) }
            .toList()
    }

    val tracks: MutableList<Track> = mutableListOf()

    init {
        tracks.addAll(resolveTracks())
    }

    override fun download() {
        for(track: Track in tracks) {
            track.download()
        }
    }

    fun offset(offset: Int?) : String {
        return if (offset != null) "?offset=$offset" else ""
    }


}