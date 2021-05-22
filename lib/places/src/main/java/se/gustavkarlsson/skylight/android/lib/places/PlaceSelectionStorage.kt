package se.gustavkarlsson.skylight.android.lib.places

internal interface PlaceSelectionStorage {
    fun saveId(id: PlaceId)
    suspend fun loadId(): PlaceId
}
