package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class TrackResponse(
    val album: AlbumResponse,
    val artists: List<ArtistResponse>,
    @field:JsonProperty("available_markets") val availableMarkets: List<String>?,
    @field:JsonProperty("disc_number") val discNumber: Int,
    @field:JsonProperty("duration_ms") val durationMs: Int,
    val episode: Boolean,
    val explicit: Boolean,
    @field:JsonProperty("external_ids") val externalIds: Map<String, String>,
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    @field:JsonProperty("is_local") val isLocal: Boolean,
    @field:JsonProperty("is_playable") val isPlayable: Boolean,
    val name: String,
    val popularity: Int?,
    @field:JsonProperty("preview_url") val previewUrl: String?,
    val restrictions: Map<String, String>?,
    @field:JsonProperty("track_number") val trackNumber: Int,
    val type: String,
    val uri: String
)