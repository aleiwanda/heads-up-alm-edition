package at.alm.headsup.datasource

import android.content.Context
import android.util.Log
import at.alm.headsup.response.DeepPlaylistResponse
import at.alm.headsup.response.ShallowPlaylistResponse
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.nio.file.Files

class LocalDataSource(private val context: () -> Context?) {
    val TAG = this.javaClass.simpleName
    private val objectMapper = jacksonObjectMapper()
    private var playlistMap = HashMap<String, DeepPlaylistResponse>()
    private var initialized = false

    fun getPlaylists(): List<ShallowPlaylistResponse> {
        if (!initialized) {
            loadPlaylistsFromDisk()
            initialized = true
        }
        return playlistMap.values.map { a -> a.toShallowPlaylistResponse() }
    }

    fun getPlaylist(playlistId: String): DeepPlaylistResponse? {
        return playlistMap[playlistId]
    }

    fun storePlaylist(playlist: DeepPlaylistResponse) {
        playlistMap[playlist.id] = playlist
        context()?.let { context ->
            try {
                val dir = context.getDir("playlists", Context.MODE_PRIVATE)
                Files.newOutputStream(
                    dir.toPath().resolve(
                        "${playlist.id}.json",
                    )
                ).use {
                    objectMapper.writeValue(it, playlist)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while storing playlist ${playlist.id}", e)
            }
        }
    }

    fun loadPlaylistsFromDisk() {
        context()?.let { context ->
            playlistMap.clear()

            val dir = context.getDir("playlists", Context.MODE_PRIVATE)
            val files = Files.list(dir.toPath())
            Log.i(TAG, files.toString())
            for (file in files) {
                if (!file.toString().endsWith(".json")) {
                    Log.i(TAG, "Skipped $file because it does not start with playlists/")
                    continue
                }
                try {
                    Files.newInputStream(file).use {
                        val playlist = objectMapper.readValue(it, DeepPlaylistResponse::class.java)
                        playlistMap[playlist.id] = playlist
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error while parsing file $file", e)
                }
            }
        }
    }
}