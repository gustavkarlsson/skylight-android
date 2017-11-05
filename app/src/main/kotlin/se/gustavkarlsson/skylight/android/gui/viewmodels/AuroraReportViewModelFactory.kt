package se.gustavkarlsson.skylight.android.gui.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import javax.inject.Inject

@Reusable
class AuroraReportViewModelFactory
@Inject
constructor(
	private val auroraReportLiveData: LiveData<AuroraReport>
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return AuroraReportViewModel(auroraReportLiveData) as T
	}

	companion object {
		val CLASS = AuroraReportViewModel::class.java
	}
}

