package at.alm.headsup.response

class TrackListResponse(
    href: String?,
    items: List<PlaylistTrackResponse>,
    limit: Int,
    next: String?,
    offset: Int,
    previous: String?,
    total: Int
) : GenericListResponse<PlaylistTrackResponse>(
    href,
    items,
    limit,
    next,
    offset,
    previous,
    total
)