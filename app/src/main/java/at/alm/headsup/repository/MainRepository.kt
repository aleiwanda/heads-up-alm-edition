package at.alm.headsup.repository

import android.content.Context
import at.alm.headsup.datasource.LocalDataSource
import at.alm.headsup.datasource.SpotifyDataSource
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import at.alm.headsup.response.TokenResponse

class MainRepository(context: () -> Context?) {
    val localDataSource: LocalDataSource = LocalDataSource(context)
    val spotifyDataSource: SpotifyDataSource = SpotifyDataSource()

    fun getLocalPlaylists(): List<ShallowPlaylistResponse> {
        return localDataSource.getPlaylists()
    }

    fun getRemotePlaylists(tokenResponse: TokenResponse): List<ShallowPlaylistResponse> {
        val playlists = spotifyDataSource.getPlaylists(tokenResponse)
        return if (playlists.isSuccess) {
            playlists.getOrDefault(emptyList())
        } else {
            emptyList() // TODO
        }
    }

    fun getPlaylist(playlistId: String): DeepPlaylistResponse? {
        return localDataSource.getPlaylist(playlistId)
    }

    fun fetchAndStoreRemotePlaylist(playlistId: String, tokenResponse: TokenResponse) {
        val playlist = spotifyDataSource.getPlaylist(playlistId, tokenResponse)
        if (playlist.isSuccess) {
            localDataSource.storePlaylist(playlist.getOrThrow())
        } else {
            TODO()
        }
    }
}