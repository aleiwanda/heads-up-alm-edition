package at.alm.headsup

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds


enum class LoginState { LOGGED_OUT, LOGGING_IN, LOGGED_IN }
class SpotifyConnector(val activity: () -> MainActivity?) {
    companion object {
        val requestCode = (Math.random() * Int.MAX_VALUE).toInt()
    }

    val TAG = this.javaClass.simpleName
    private val REDIRECT_URI = "alm://alm.at/callback"
    private val CLIENT_ID = "36942e0c9fb14c5daad41011cccd44eb"
    private val CLIENT_SECRET = "6471d5a9d4d34f498bde15fa4827b7a8"
    private val objectMapper = jacksonObjectMapper()
    var inLoginProcess = false
    var tokenResponseContent by mutableStateOf(Result.failure<TokenResponse>(NoTokenException()))
        private set

    fun openLoginWindow() {
        activity()?.let { activity ->
            inLoginProcess = true
            activity.spotifyLoginCallback = { resultCode, data ->
                this.continueAuthorization(resultCode, data)
            }
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
    }

    fun logout() {
        this.tokenResponseContent = Result.failure(NoTokenException())
        // TODO implement proper logout
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
                    objectMapper.readValue(tokenResponse.content, TokenResponse::class.java)

                )
                storeToken(tokenResponse.content)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unhandled exception caught", e)
            tokenResponseContent = Result.failure(e)
        }
        inLoginProcess = false
    }

    fun storeToken(tokenResponse: ByteArray) {
        activity()?.let { activity ->
            try {
                activity.openFileOutput(".spotify_token", Context.MODE_PRIVATE).use {
                    it.write(tokenResponse)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while storing Spotify token", e)
            }
        }
    }

    fun loadTokenFromDisk() {
        activity()?.let { activity ->
            try {
                activity.openFileInput(".spotify_token").use {
                    tokenResponseContent = Result.success(
                        objectMapper.readValue(it.readAllBytes(), TokenResponse::class.java)
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception while loading Spotify token", e)
                tokenResponseContent = Result.failure(e)
            }
        }
    }

    fun getLoginState(): LoginState {
        val tokenResponse = tokenResponseContent.getOrNull()
        if (inLoginProcess) {
            return LoginState.LOGGING_IN
        }
        if (tokenResponse != null && tokenResponse.responseTimestamp.plus(tokenResponse.expiresIn.seconds) > Clock.System.now()) {
            return LoginState.LOGGED_IN
        }
        return LoginState.LOGGED_OUT
    }

    class NoTokenException : RuntimeException()
    class InvalidTokenResponseException(message: String?) : RuntimeException(message)
}