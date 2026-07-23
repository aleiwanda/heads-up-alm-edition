package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.time.Clock
import kotlin.time.Instant

data class TokenResponse(
    val responseTimestamp: Instant = Clock.System.now(),
    @field:JsonProperty("access_token") var accessToken: String,
    @field:JsonProperty("token_type") var tokenType: String,
    var scope: String,
    @field:JsonProperty("expires_in") var expiresIn: Int,
    @field:JsonProperty("refresh_token") var refreshToken: String,
)
