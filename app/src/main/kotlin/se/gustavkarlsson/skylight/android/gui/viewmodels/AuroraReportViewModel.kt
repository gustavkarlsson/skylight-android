package se.gustavkarlsson.skylight.android.gui.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.services.evaluation.Chance

class AuroraReportViewModel(
	val chanceLevel: LiveData<String>,
	val locationName: LiveData<String>,
	val timestamp: LiveData<Instant>,
	val kpIndex: LiveData<Factor>,
	val location: LiveData<Factor>,
	val visibility: LiveData<Factor>,
	val darkness: LiveData<Factor>
) : ViewModel() {
	class Factor(
		val value: String,
		val chance: Chance
	)
}
