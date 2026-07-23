package at.alm.headsup.response

class DeepPlaylistResponse(
    collaborative: Boolean,
    description: String,
    externalUrls: Map<String, String>,
    followers: GenericItemResponse,
    href: String,
    id: String,
    images: List<ImageResponse>,
    items: TrackListResponse,
    name: String,
    owner: UserResponse,
    primaryColor: String?,
    public: Boolean,
    snapshotId: String,
    type: String,
    uri: String
) : PlaylistResponse<TrackListResponse>(
    collaborative,
    description,
    externalUrls,
    followers,
    href,
    id,
    images,
    items,
    name,
    owner,
    primaryColor,
    public,
    snapshotId,
    type,
    uri
) {
    fun toShallowPlaylistResponse(): ShallowPlaylistResponse {
        return ShallowPlaylistResponse(
            collaborative,
            description,
            externalUrls,
            followers,
            href,
            id,
            images,
            GenericItemResponse(items.href, items.total),
            name,
            owner,
            primaryColor,
            public,
            snapshotId,
            type,
            uri,
        )
    }
}