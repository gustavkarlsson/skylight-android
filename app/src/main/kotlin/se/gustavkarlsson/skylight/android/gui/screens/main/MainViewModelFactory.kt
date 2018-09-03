package se.gustavkarlsson.skylight.android.gui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Single
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class MainViewModelFactory(
	private val store: SkylightStore,
	private val defaultLocationName: CharSequence,
	private val notConnectedToInternetMessage: CharSequence,
	private val auroraChanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val relativeTimeFormatter: RelativeTimeFormatter,
	private val chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
	private val darknessChanceEvaluator: ChanceEvaluator<Darkness>,
	private val darknessFormatter: SingleValueFormatter<Darkness>,
	private val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
	private val geomagLocationFormatter: SingleValueFormatter<GeomagLocation>,
	private val kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
	private val kpIndexFormatter: SingleValueFormatter<KpIndex>,
	private val weatherChanceEvaluator: ChanceEvaluator<Weather>,
	private val weatherFormatter: SingleValueFormatter<Weather>,
	private val now: Single<Instant>,
	private val rightNowThreshold: Duration
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel> create(modelClass: Class<T>): T {
		require(modelClass == CLASS) { "Unsupported ViewModel class: $modelClass, expected: $CLASS" }
		return MainViewModel(
			store,
			defaultLocationName,
			notConnectedToInternetMessage,
			auroraChanceEvaluator,
			relativeTimeFormatter,
			chanceLevelFormatter,
			darknessChanceEvaluator,
			darknessFormatter,
			geomagLocationChanceEvaluator,
			geomagLocationFormatter,
			kpIndexChanceEvaluator,
			kpIndexFormatter,
			weatherChanceEvaluator,
			weatherFormatter,
			now,
			rightNowThreshold
		) as T
	}

	companion object {
		private val CLASS = MainViewModel::class.java
	}
}

