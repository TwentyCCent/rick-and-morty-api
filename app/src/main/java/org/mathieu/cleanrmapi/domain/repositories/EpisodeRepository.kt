package org.mathieu.cleanrmapi.domain.repositories

import kotlinx.coroutines.flow.Flow
import org.mathieu.cleanrmapi.domain.models.episode.Episode

interface EpisodeRepository {
    suspend fun getEpisodesByIds(ids: List<String>): Flow<List<Episode>>

    suspend fun getEpisode(id: Int): Episode
}