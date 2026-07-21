package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class ArtistResponse(
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)