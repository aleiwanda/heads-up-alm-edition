package at.alm.headsup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import at.alm.headsup.response.PlaylistResponse

enum class LoginProcess { LOGGED_OUT, REQUESTED, INITIATED, LOGGED_IN }

interface MainUIState {
    val loginState: LoginProcess
    val playlists: List<PlaylistResponse>
    val playlistsReloading: Boolean
}

class MutableMainUIState: MainUIState {
    override var loginState: LoginProcess by mutableStateOf(LoginProcess.LOGGED_OUT)
    override var playlists: List<PlaylistResponse> = emptyList<PlaylistResponse>().toMutableStateList()
    override var playlistsReloading: Boolean by mutableStateOf(false)
}
