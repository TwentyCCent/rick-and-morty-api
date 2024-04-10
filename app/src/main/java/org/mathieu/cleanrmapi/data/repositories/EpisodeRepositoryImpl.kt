package org.mathieu.cleanrmapi.data.repositories

import android.content.Context
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import org.mathieu.cleanrmapi.data.local.EpisodeLocal
import org.mathieu.cleanrmapi.data.local.objects.EpisodeObject
import org.mathieu.cleanrmapi.data.local.objects.toModel
import org.mathieu.cleanrmapi.data.local.objects.toRealmObject
import org.mathieu.cleanrmapi.data.remote.EpisodeApi
import org.mathieu.cleanrmapi.data.remote.responses.EpisodeResponse
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository

internal class EpisodeRepositoryImpl(
    private val context: Context,
    private val episodeApi: EpisodeApi,
    private val episodeLocal: EpisodeLocal
) : EpisodeRepository {
    override suspend fun getEpisodesByIds(ids: List<String>): Flow<List<Episode>> {
        val response = episodeApi.getEpisodes(ids)
        val remoteEpisodes = response.map(transform = EpisodeResponse::toRealmObject)
        episodeLocal.saveEpisodes(remoteEpisodes)
        return  episodeLocal.getEpisodes(ids)
            .mapElement(transform = EpisodeObject::toModel)
            /*.also { if (it.first().isEmpty()) {
                val yo = it.count()
                val yu = ids.count()
                fetchEpisodeIfIsNotCached(ids)
            } }*/
    }

    private suspend fun fetchEpisodeIfIsNotCached(ids: List<String>) {
        val response = episodeApi.getEpisodes(ids)
        val remoteEpisodes = response.map(transform = EpisodeResponse::toRealmObject)
        episodeLocal.saveEpisodes(remoteEpisodes)
    }

    override suspend fun getEpisode(id: Int): Episode =
        episodeLocal.getEpisode(id)?.toModel()
            ?: episodeApi.getEpisode(id = id)?.let { response ->
                val obj = response.toRealmObject()
                episodeLocal.insert(obj)
                obj.toModel()
            }
            ?: throw Exception("Episode not found.")
}