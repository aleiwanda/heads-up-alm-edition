package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class AlbumResponse(
    @field:JsonProperty("album_type") val albumType: String,
    val artists: List<ArtistResponse>,
    @field:JsonProperty("available_markets") val availableMarkets: List<String>?,
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val images: List<ImageResponse>,
    @field:JsonProperty("is_playable") val isPlayable: Boolean,
    val name: String,
    @field:JsonProperty("release_date") val releaseDate: String,
    @field:JsonProperty("release_date_precision") val releaseDatePrecision: String,
    val restrictions: Map<String, String>?,
    @field:JsonProperty("total_tracks") val totalTracks: Int,
    val type: String,
    val uri: String
)