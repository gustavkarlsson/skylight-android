package se.gustavkarlsson.skylight.android.gui.activities.main

import com.hadisatrio.optional.Optional
import com.jakewharton.rxrelay2.PublishRelay
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class MainViewModel(
	auroraReportSingle: Single<AuroraReport>,
	auroraReports: Flowable<AuroraReport>,
	isConnectedToInternet: Flowable<Boolean>,
	defaultLocationName: CharSequence,
	notConnectedToInternetMessage: CharSequence
) : AutoDisposableViewModel() {

	private val errorRelay = PublishRelay.create<Throwable>()
	val errorMessages: Flowable<Int> = errorRelay
		.toFlowable(BackpressureStrategy.BUFFER)
		.map {
			if (it is UserFriendlyException) {
				it.stringResourceId
			} else {
				R.string.error_unknown_update_error
			}
		}

	val connectivityMessages: Flowable<Optional<CharSequence>> =
		Flowable.concat(Flowable.just(true), isConnectedToInternet)
			.map { connected ->
				if (connected) {
					Optional.absent()
				} else {
					Optional.of(notConnectedToInternetMessage)
				}
			}
			.distinctUntilChanged()

	val locationName: Flowable<CharSequence> = auroraReports
		.map {
			it.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val refresh: Consumer<Unit> = Consumer {
		auroraReportSingle
			.doOnEvent { _, _ -> refreshFinishedRelay.accept(Unit) }
			.autoDisposable(scope())
			.subscribe(Consumer {}, errorRelay)
	}

	private val refreshFinishedRelay = PublishRelay.create<Unit>()
	val refreshFinished: Flowable<Unit> = refreshFinishedRelay
		.toFlowable(BackpressureStrategy.LATEST)
}
