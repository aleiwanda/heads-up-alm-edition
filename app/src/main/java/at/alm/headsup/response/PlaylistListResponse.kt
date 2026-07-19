package at.alm.headsup.response

data class PlaylistListResponse(
    var href: String?,
    var limit: Int,
    var next: String?,
    var offset: Int,
    var previous: String?,
    var total: Int,
    var items: List<PlaylistResponse>
)