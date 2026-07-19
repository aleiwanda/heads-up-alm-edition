package at.alm.headsup

import android.content.Intent
import android.util.Log
import at.alm.headsup.datasource.SpotifyDataSource
import at.alm.headsup.response.TokenResponse
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import org.eclipse.jetty.client.FormRequestContent
import org.eclipse.jetty.client.HttpClient
import org.eclipse.jetty.http.HttpMethod
import org.eclipse.jetty.util.Fields
import tools.jackson.module.kotlin.jacksonObjectMapper
import kotlin.io.encoding.Base64

class SpotifyConnector {
    val TAG = this.javaClass.simpleName
    private val REDIRECT_URI = "alm://alm.at/callback"
    private val CLIENT_ID = "36942e0c9fb14c5daad41011cccd44eb"
    private val CLIENT_SECRET = "6471d5a9d4d34f498bde15fa4827b7a8"
    val requestCode = (Math.random() * Int.MAX_VALUE).toInt()
    private val objectMapper = jacksonObjectMapper()
    var tokenResponseContent: Result<TokenResponse> = Result.failure(NoTokenException())
        private set

    fun openLoginWindow(activity: MainActivity) {
        val request = AuthorizationRequest.Builder(
            CLIENT_ID, AuthorizationResponse.Type.CODE,
            REDIRECT_URI
        )
            .setScopes(arrayOf("playlist-read-private", "playlist-read-collaborative")).build()
        try {
            AuthorizationClient.openLoginActivity(
                activity,
                requestCode,
                request
            )
        } catch (e: Exception) {
            Log.e(SpotifyDataSource::class.java.simpleName, "Exception caught", e)
        }
    }

    fun continueAuthorization(resultCode: Int, data: Intent?) {
        val codeResponse = AuthorizationClient.getResponse(resultCode, data)

        if (codeResponse.type != AuthorizationResponse.Type.CODE) {
            Log.e(TAG, "Error while fetching Spotify access token, $codeResponse")
            return
        }

        Log.i(TAG, "Successfully fetched Spotify access code, ${codeResponse.code}")

        try {
            HttpClient().use { client ->
                client.start()
                val fields = Fields()
                fields.add("code", codeResponse.code)
                fields.add("redirect_uri", REDIRECT_URI)
                fields.add("grant_type", "authorization_code")
                val tokenResponse = client.newRequest("https://accounts.spotify.com/api/token")
                    .body(FormRequestContent(fields))
                    .headers { h ->
                        h.add(
                            "Authorization",
                            "Basic " + Base64.encode("${CLIENT_ID}:${CLIENT_SECRET}".toByteArray())
                        ).add("Content-Type", "application/x-www-form-urlencoded")
                    }
                    .method(HttpMethod.POST)
                    .send()

                if (tokenResponse.status != 200) {
                    tokenResponseContent =
                        Result.failure(InvalidTokenResponseException("Spotify API responded with ${tokenResponse.status}: ${tokenResponse.contentAsString}"))
                }
                tokenResponseContent = Result.success(
                    objectMapper.readValue(tokenResponse.contentAsString, TokenResponse::class.java)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled exception caught", e)
            tokenResponseContent = Result.failure(e)
        }
    }

    class NoTokenException : RuntimeException()
    class InvalidTokenResponseException(message: String?) : RuntimeException(message)
}