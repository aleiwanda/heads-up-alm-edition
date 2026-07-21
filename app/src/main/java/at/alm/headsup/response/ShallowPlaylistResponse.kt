package at.alm.headsup.response

class ShallowPlaylistResponse(
    collaborative: Boolean,
    description: String,
    externalUrls: Map<String, String>,
    followers: GenericItemResponse?,
    href: String,
    id: String,
    images: List<ImageResponse>,
    items: GenericItemResponse,
    name: String,
    owner: UserResponse,
    primaryColor: String?,
    public: Boolean,
    snapshotId: String,
    type: String,
    uri: String
) : PlaylistResponse<GenericItemResponse>(
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
)