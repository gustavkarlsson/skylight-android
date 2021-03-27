package se.gustavkarlsson.skylight.android.lib.places

internal interface PlaceSelectionStorage {
    fun saveId(id: Long?)
    suspend fun loadId(): Long?
}
