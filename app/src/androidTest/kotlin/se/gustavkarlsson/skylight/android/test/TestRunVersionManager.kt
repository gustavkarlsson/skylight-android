package se.gustavkarlsson.skylight.android.test

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.services.RunVersionManager

class TestRunVersionManager(
	initialIsFirstRun: Boolean
) : RunVersionManager {

	val isFirstRunRelay = BehaviorRelay.createDefault(initialIsFirstRun)

	override val isFirstRun: Flowable<Boolean> =
		isFirstRunRelay.toFlowable(BackpressureStrategy.LATEST)

	override fun signalFirstRunCompleted() = isFirstRunRelay.accept(false)

}
