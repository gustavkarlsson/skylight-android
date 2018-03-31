package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.dagger.qualifiers.ConnectedToInternet
import se.gustavkarlsson.skylight.android.dagger.qualifiers.DefaultLocationName
import se.gustavkarlsson.skylight.android.dagger.qualifiers.NotConnectedToInternet
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import javax.inject.Inject

@Reusable
class MainViewModelFactory
@Inject
constructor(
	private val auroraReportSingle: Single<AuroraReport>,
	private val auroraReports: Flowable<AuroraReport>,
	@ConnectedToInternet private val isConnectedToInternet: Flowable<Boolean>,
	@DefaultLocationName private val defaultLocationName: CharSequence,
	@NotConnectedToInternet private val notConnectedToInternetMessage: CharSequence
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return MainViewModel(
			auroraReportSingle,
			auroraReports,
			isConnectedToInternet,
			defaultLocationName,
			notConnectedToInternetMessage
		) as T
	}

	companion object {
		private val CLASS = MainViewModel::class.java
	}
}

