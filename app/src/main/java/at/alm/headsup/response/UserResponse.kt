package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val type: String,
    val uri: String,
    @field:JsonProperty("display_name") val displayName: String
)
