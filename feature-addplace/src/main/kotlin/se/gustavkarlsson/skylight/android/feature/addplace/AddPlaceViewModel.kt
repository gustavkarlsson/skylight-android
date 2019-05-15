package se.gustavkarlsson.skylight.android.feature.addplace

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.debounce
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Geocoder

internal class AddPlaceViewModel(
	geocoder: Geocoder,
	debounceDelay: Duration,
	retryDelay: Duration
) : ViewModel() {

	private val searchStringRelay = BehaviorRelay.create<String>()
	private val loadingRelay = BehaviorRelay.createDefault(false)
	private val dialogRelay = PublishRelay.create<Pair<String, Location>>()

	private var searchCount = 0L

	fun onSearchTextChanged(newText: String) = searchStringRelay.accept(newText)

	val searchResultItems: Flowable<List<SearchResultItem>> =
		searchStringRelay
			.map {
				loadingRelay.accept(true)
				++searchCount to it
			}
			.toFlowable(BackpressureStrategy.LATEST)
			.debounce(debounceDelay)
			.switchMapSingle { (searchNumber, text) ->
				geocoder.geocode(text)
					.doOnSuccess {
						if (searchCount == searchNumber) {
							loadingRelay.accept(false)
						}
					}
			}
			.retryWhen { it.delay(retryDelay) }
			.map { suggestions ->
				suggestions.map {
					SearchResultItem(it.fullName) {
						dialogRelay.accept(it.simpleName to it.location)
					}
				}
			}
			.observeOn(AndroidSchedulers.mainThread())

	val isLoading: Observable<Boolean> =
		loadingRelay
			.observeOn(AndroidSchedulers.mainThread())

	val openSaveDialog: Flowable<Pair<String, Location>> =
		dialogRelay
			.toFlowable(BackpressureStrategy.LATEST)
			.observeOn(AndroidSchedulers.mainThread())
}
