package at.alm.headsup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse

enum class LoginProcess { LOGGED_OUT, REQUESTED, INITIATED, LOGGED_IN }

interface MainUIState {
    val loginState: LoginProcess
    val playlists: List<ShallowPlaylistResponse>
    val playlistsReloading: Boolean
    val currentPlaylist: DeepPlaylistResponse?
}

class MutableMainUIState : MainUIState {
    override var loginState: LoginProcess by mutableStateOf(LoginProcess.LOGGED_OUT)
    override var playlists: List<ShallowPlaylistResponse> =
        emptyList<ShallowPlaylistResponse>().toMutableStateList()
    override var playlistsReloading: Boolean by mutableStateOf(false)
    override var currentPlaylist: DeepPlaylistResponse? by mutableStateOf(null)
}
