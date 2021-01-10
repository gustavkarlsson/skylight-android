package se.gustavkarlsson.skylight.android.lib.places

internal interface PlaceSelectionStorage {
    fun saveIndex(index: Int)
    suspend fun loadIndex(): Int?
}
