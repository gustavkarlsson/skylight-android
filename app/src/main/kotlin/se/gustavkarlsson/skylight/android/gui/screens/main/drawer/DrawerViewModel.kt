package se.gustavkarlsson.skylight.android.gui.screens.main.drawer

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Place
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class DrawerViewModel(
	store: SkylightStore
) : ViewModel() {
	val places: Flowable<List<Place>> =
		store.states
			.map {
				it.customPlaces
			}
			.distinctUntilChanged()
}
