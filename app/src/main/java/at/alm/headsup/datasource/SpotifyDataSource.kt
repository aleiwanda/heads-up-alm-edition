package at.alm.headsup.datasource

import android.util.Log
import at.alm.headsup.response.PlaylistListResponse
import at.alm.headsup.response.PlaylistResponse
import at.alm.headsup.response.TokenResponse
import org.eclipse.jetty.client.ContentResponse
import org.eclipse.jetty.client.HttpClient
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.Collections

class SpotifyDataSource {
    val TAG = this.javaClass.simpleName
    private val objectMapper = jacksonObjectMapper()

    fun getPlaylists(tokenResponse: TokenResponse): Result<List<PlaylistResponse>> {
        val playlists = ArrayList<PlaylistResponse>()

        var responseValue = PlaylistListResponse(
            null,
            0,
            "https://api.spotify.com/v1/me/playlists",
            0,
            null,
            0,
            Collections.emptyList()
        )

        try {
            HttpClient().use { client ->
                client.start()
                while (responseValue.next != null) {
                    val response: ContentResponse = client.newRequest(responseValue.next)
                        .headers { a ->
                            a.add(
                                "Authorization",
                                "${tokenResponse.tokenType} ${tokenResponse.accessToken}"
                            )
                        }
                        .send()

                    if (response.status != 200) {
                        return Result.failure(RuntimeException("Spotify API responded with ${response.status}: ${response.contentAsString}"))
                    }
                    responseValue =
                        objectMapper.readValue(response.content, PlaylistListResponse::class.java)
                    playlists.addAll(responseValue.items)
                }
            }
            return Result.success(playlists)
        } catch (e: Exception) {
            Log.e(TAG, "Exception caught", e)
            return Result.failure(e)
        }
    }
}