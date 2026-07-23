package at.alm.headsup.response

import com.fasterxml.jackson.annotation.JsonProperty

abstract class PlaylistResponse<T>(
    val collaborative: Boolean,
    val description: String,
    @field:JsonProperty("external_urls") val externalUrls: Map<String, String>,
    val followers: GenericItemResponse?,
    val href: String,
    val id: String,
    val images: List<ImageResponse>,
    val items: T?,
    val name: String,
    val owner: UserResponse,
    @field:JsonProperty("primary_color") val primaryColor: String?,
    val public: Boolean,
    @field:JsonProperty("snapshot_id") val snapshotId: String,
    val type: String,
    val uri: String
)