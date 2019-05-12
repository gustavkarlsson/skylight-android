package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Geocoder
import timber.log.Timber
import java.util.concurrent.TimeUnit

// FIXME create store for this feature, and handle loading, reverse geocoding, and so on.
internal class AddPlaceViewModel(geocoder: Geocoder) : ViewModel() {

	private val searchStringRelay = BehaviorRelay.create<String>()

	private val dialogRelay = PublishRelay.create<Pair<String, Location>>()

	fun onSearchTextChanged(newText: String) = searchStringRelay.accept(newText)

	val searchResultItems: Flowable<List<SearchResultItem>> =
		searchStringRelay
			.debounce(1, TimeUnit.SECONDS)
			.toFlowable(BackpressureStrategy.LATEST)
			.flatMapSingle(geocoder::geocode)
			.map { suggestions ->
				suggestions.map {
					SearchResultItem(it.fullName) {
						dialogRelay.accept(it.simpleName to it.location)
					}
				}
			}
			.observeOn(AndroidSchedulers.mainThread())

	val openSaveDialog: Flowable<Pair<String, Location>> =
		dialogRelay
			.toFlowable(BackpressureStrategy.LATEST)
			.observeOn(AndroidSchedulers.mainThread())
}
