package se.gustavkarlsson.skylight.android.lib.places

import io.reactivex.Observable

interface SelectedPlaceRepository {
    fun set(place: Place)
    fun get(): Place
    fun stream(): Observable<Place>
}
