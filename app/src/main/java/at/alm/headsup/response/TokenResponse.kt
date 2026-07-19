package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenResponse(
    @field:JsonProperty("access_token") var accessToken: String,
    @field:JsonProperty("token_type") var tokenType: String,
    var scope: String,
    @field:JsonProperty("expires_in") var expiresIn: Int,
    @field:JsonProperty("refresh_token") var refreshToken: String,
)
