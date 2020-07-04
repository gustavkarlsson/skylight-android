package se.gustavkarlsson.skylight.android.lib.places

internal interface PlaceSelectionStorage {
    fun saveIndex(index: Int)
    fun loadIndex(): Int?
}
