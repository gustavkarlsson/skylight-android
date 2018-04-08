package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport

class MainViewModelFactory(
	private val auroraReportSingle: Single<AuroraReport>,
	private val auroraReports: Flowable<AuroraReport>,
	private val isConnectedToInternet: Flowable<Boolean>,
	private val defaultLocationName: CharSequence,
	private val notConnectedToInternetMessage: CharSequence
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

