package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds

// FIXME create store for this feature, and handle loading, reverse geocoding, and so on.
internal class AddPlaceViewModel : ViewModel() {

	fun onSearchTextChanged(newText: String) {

	}

	val searchResultItems: Flowable<List<SearchResultItem>> =
		Flowable.just(
			listOf(
				SearchResultItem("Some place", {}),
				SearchResultItem("Some other", {}),
				SearchResultItem("Some third", {}),
				SearchResultItem("Some forth", {}),
				SearchResultItem("Some fifth", {}),
				SearchResultItem("Some sixth", {}),
				SearchResultItem("Some sixth", {}),
				SearchResultItem("Some 7", {}),
				SearchResultItem("Some 8", {}),
				SearchResultItem("Some 9", {}),
				SearchResultItem("Last place", {}),
				SearchResultItem("Last place", {})
			)
		)
			.delay(3.seconds)
			.observeOn(AndroidSchedulers.mainThread())

	val openSaveDialog: Flowable<Pair<String, Location>> =
		Flowable.just("Some dumb place" to Location(1.2, 5.4))
			.delay(5.seconds)
			.observeOn(AndroidSchedulers.mainThread())
}
