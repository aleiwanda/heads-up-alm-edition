package at.alm.headsup.datasource

import at.alm.headsup.response.PlaylistResponse

class LocalDataSource {
    private var playlistList = emptyList<PlaylistResponse>()

    fun getPlaylists(): List<PlaylistResponse> {
        return playlistList
    }
    fun setPlaylists(list: List<PlaylistResponse>) {
        playlistList = list;
    }
}