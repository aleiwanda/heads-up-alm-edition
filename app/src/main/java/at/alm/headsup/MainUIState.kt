package at.alm.headsup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

interface MainUIState {
    val localPlaylists: List<ShallowPlaylistResponse>
    val remotePlaylists: List<ShallowPlaylistResponse>
    val playlistsReloading: Boolean
    val selectedPlaylist: DeepPlaylistResponse?
    var duration: Duration
    var timeLeft: Int
}

class MutableMainUIState : MainUIState {
    override var localPlaylists: List<ShallowPlaylistResponse> by mutableStateOf(emptyList())
    override var remotePlaylists: List<ShallowPlaylistResponse> by mutableStateOf(emptyList())
    override var playlistsReloading: Boolean by mutableStateOf(false)
    override var selectedPlaylist: DeepPlaylistResponse? by mutableStateOf(null)
    override var duration: Duration by mutableStateOf(60.toDuration(DurationUnit.SECONDS))
    override var timeLeft: Int by mutableIntStateOf(0)
}
