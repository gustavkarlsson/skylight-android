package se.gustavkarlsson.skylight.android.lib.places

import android.content.Intent

private const val KEY_PLACE_ID = "placeId"

fun Intent.setPlaceId(placeId: PlaceId) {
    putExtra(KEY_PLACE_ID, placeId.value)
}

fun Intent.getPlaceId(): PlaceId? {
    val value = getLongExtra(KEY_PLACE_ID, Long.MIN_VALUE)
    return if (value == Long.MIN_VALUE) {
        null
    } else PlaceId.fromLong(value)
}
