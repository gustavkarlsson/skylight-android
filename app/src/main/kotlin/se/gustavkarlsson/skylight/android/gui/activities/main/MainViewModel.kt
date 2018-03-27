package se.gustavkarlsson.skylight.android.gui.activities.main

import com.jakewharton.rxrelay2.PublishRelay
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.AutoDisposableViewModel

class MainViewModel(
	auroraReportSingle: Single<AuroraReport>,
	auroraReports: Flowable<AuroraReport>,
	postAuroraReport: Consumer<AuroraReport>,
	defaultLocationName: CharSequence
) : AutoDisposableViewModel() {
	val locationName: Flowable<CharSequence> = auroraReports
		.map {
			it.locationName ?: defaultLocationName
		}
		.distinctUntilChanged()

	val refresh: Consumer<Unit> = Consumer {
		auroraReportSingle
			.doOnEvent { _, _ -> refreshFinishedRelay.accept(Unit) }
			.autoDisposable(scope())
			.subscribe(postAuroraReport)
	}

	private val refreshFinishedRelay = PublishRelay.create<Unit>()
	val refreshFinished: Flowable<*> = refreshFinishedRelay
		.toFlowable(BackpressureStrategy.LATEST)
}
