package at.alm.headsup.repository

import at.alm.headsup.datasource.LocalDataSource
import at.alm.headsup.datasource.SpotifyDataSource
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import at.alm.headsup.response.TokenResponse

class MainRepository {
    val localDataSource: LocalDataSource = LocalDataSource()
    val spotifyDataSource: SpotifyDataSource = SpotifyDataSource()

    fun getPlaylists(): List<ShallowPlaylistResponse> {
        return localDataSource.getPlaylists()
    }

    fun reloadPlaylists(tokenResponse: TokenResponse) {
        val playlists = spotifyDataSource.getPlaylists(tokenResponse)
        if (playlists.isSuccess) {
            localDataSource.setPlaylists(playlists.getOrThrow())
        } else {
            TODO()
        }
    }

    fun getPlaylist(playlistId: String): DeepPlaylistResponse? {
        return localDataSource.getPlaylist(playlistId)
    }

    fun reloadPlaylist(playlistId: String, tokenResponse: TokenResponse) {
        val playlist = spotifyDataSource.getPlaylist(playlistId, tokenResponse)
        if (playlist.isSuccess) {
            localDataSource.setPlaylist(playlistId, playlist.getOrThrow())
        } else {
            TODO()
        }
    }
}