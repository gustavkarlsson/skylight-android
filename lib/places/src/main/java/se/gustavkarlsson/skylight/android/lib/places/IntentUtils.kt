package se.gustavkarlsson.skylight.android.lib.places

import android.os.Bundle

private const val KEY_PLACE_ID = "placeId"

fun Bundle.setPlaceId(placeId: PlaceId) {
    putLong(KEY_PLACE_ID, placeId.value)
}

fun Bundle.getPlaceId(): PlaceId? {
    val value = getLong(KEY_PLACE_ID, Long.MIN_VALUE)
    return if (value == Long.MIN_VALUE) {
        null
    } else PlaceId.fromLong(value)
}
