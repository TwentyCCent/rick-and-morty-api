package org.mathieu.cleanrmapi.ui.screens.characterdetails

import android.app.Application
import android.util.Log
import androidx.compose.material3.Text
import org.koin.core.component.inject
import org.mathieu.cleanrmapi.domain.models.character.Character
import org.mathieu.cleanrmapi.domain.models.episode.Episode
import org.mathieu.cleanrmapi.domain.repositories.CharacterRepository
import org.mathieu.cleanrmapi.domain.repositories.EpisodeRepository
import org.mathieu.cleanrmapi.ui.core.Destination
import org.mathieu.cleanrmapi.ui.core.ViewModel
import org.mathieu.cleanrmapi.ui.screens.characters.CharactersAction


sealed interface EpisodesAction {
    data class SelectedEpisode(val episode: Episode): EpisodesAction
}

class CharacterDetailsViewModel(application: Application) : ViewModel<CharacterDetailsState>(CharacterDetailsState(), application) {

    private val characterRepository: CharacterRepository by inject()
    private val episodeRepository: EpisodeRepository by inject()

    private var transform: (String) -> String = {it.split("/").last()}
    fun init(characterId: Int) {
        fetchData(
            source = { characterRepository.getCharacter(id = characterId) }
        ) {

            onSuccess {

                updateState {
                    copy(
                        avatarUrl = it.avatarUrl,
                        name = it.name,
                        error = null)
                }

                collectData(
                    source = { episodeRepository.getEpisodesByIds(ids =  it.episode.map(transform)) }
                ) {

                    onSuccess {
                        updateState {

                            copy(
                                episodes = it,
                                error = null) }
                    }

                    onFailure {
                        updateState { copy(error = it.toString()) }
                    }

                    updateState { copy(isLoading = false) }
                }
            }

            onFailure {
                updateState { copy(error = it.toString()) }
            }

            updateState { copy(isLoading = false) }
        }
    }

    fun handleAction(action: EpisodesAction) {
        when(action) {
            is EpisodesAction.SelectedEpisode -> selectedEpisode(action.episode)
        }
    }


    private fun selectedEpisode(episode: Episode) =
        sendEvent(Destination.EpisodeDetails(episode.id.toString()))
}


data class CharacterDetailsState(
    val isLoading: Boolean = true,
    val avatarUrl: String = "",
    val name: String = "",
    val episodes: List<Episode> = emptyList(),
    val error: String? = null
)