package at.alm.headsup.datasource

import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse

class LocalDataSource {
    private var playlistList = emptyList<ShallowPlaylistResponse>()
    private var playlistMap = HashMap<String, DeepPlaylistResponse>()

    fun getPlaylists(): List<ShallowPlaylistResponse> {
        return playlistList
    }

    fun setPlaylists(list: List<ShallowPlaylistResponse>) {
        playlistList = list
    }

    fun getPlaylist(playlistId: String): DeepPlaylistResponse? {
        return playlistMap[playlistId]
    }

    fun setPlaylist(playlistId: String, playlist: DeepPlaylistResponse) {
        playlistMap[playlistId] = playlist
    }
}