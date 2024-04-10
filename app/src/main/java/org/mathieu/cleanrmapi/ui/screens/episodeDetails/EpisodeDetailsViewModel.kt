package org.mathieu.cleanrmapi.ui.screens.episodeDetails

import android.app.Application
import org.koin.core.component.inject
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository
import org.mathieu.cleanrmapi.ui.core.ViewModel


class EpisodeDetailsViewModel(application: Application) : ViewModel<EpisodeDetailsState>(EpisodeDetailsState(), application) {

    private val episodeRepository: EpisodeRepository by inject()

    fun init(episodeId: Int) {
        fetchData(
            source = { episodeRepository.getEpisode(id = episodeId) }
        ) {

            onSuccess {
                updateState {

                    copy(
                        airDate = it.airDate,
                        episode = it.episode,
                        name = it.name,
                        error = null) }

            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }


}

data class EpisodeDetailsState(
    val isLoading: Boolean = true,
    val name: String = "",
    val airDate: String = "",
    val episode: String = "",
    val error: String? = null
)