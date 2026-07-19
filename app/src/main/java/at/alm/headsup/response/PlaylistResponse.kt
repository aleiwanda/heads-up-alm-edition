package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PlaylistResponse(
    val collaborative: Boolean,
    val description: String,
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val href: String,
    val id: String,
    val images: List<ImageResponse>,
    val name: String,
    val owner: UserResponse,
    val public: Boolean,
    @field:JsonProperty("snapshot_id") val snapshotId: String,
    val items: ItemResponse,
    val type: String,
    val uri: String
)