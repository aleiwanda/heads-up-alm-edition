package at.alm.headsup.repository

import at.alm.headsup.datasource.LocalDataSource
import at.alm.headsup.datasource.SpotifyDataSource
import at.alm.headsup.response.PlaylistResponse
import at.alm.headsup.response.TokenResponse

class MainRepository {
    val localDataSource: LocalDataSource = LocalDataSource()
    val spotifyDataSource: SpotifyDataSource = SpotifyDataSource()

    fun getPlaylists(): List<PlaylistResponse> {
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
}