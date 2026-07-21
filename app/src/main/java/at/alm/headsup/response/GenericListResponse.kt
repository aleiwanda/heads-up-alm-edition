package at.alm.headsup.response

abstract class GenericListResponse<T>(
    var href: String?,
    var items: List<T>,
    var limit: Int,
    var next: String?,
    var offset: Int,
    var previous: String?,
    var total: Int
)