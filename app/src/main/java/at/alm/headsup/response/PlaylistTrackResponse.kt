package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaylistTrackResponse(
    @field:JsonProperty("added_at") val addedAt: String,
    @field:JsonProperty("added_by") val addedBy: UserResponse,
    @field:JsonProperty("is_local") val isLocal: Boolean,
    val item: TrackResponse,
    @field:JsonProperty("primary_color") val primaryColor: String?
)