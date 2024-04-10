package org.mathieu.cleanrmapi.data.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse

internal class EpisodeApi(private val client: HttpClient){
    suspend fun getEpisodes(ids: List<String>): List<EpisodeResponse> {
        if(ids.count() > 1) {
            return client
                .get("episode/${ids.joinToString().replace(" ", "")}")
                .accept(HttpStatusCode.OK).body()
        }
        else if (ids.count() == 1) {
            val episode = client
                .get("episode/${ids.joinToString().replace(" ", "")}")
                .accept(HttpStatusCode.OK).body<EpisodeResponse>()
            return listOf<EpisodeResponse>(episode)
        }

        return listOf<EpisodeResponse>()
    }

    suspend fun getEpisode(id: Int): EpisodeResponse? = client
        .get("episode/$id")
        .accept(HttpStatusCode.OK)
        .body()

}