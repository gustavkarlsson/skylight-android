package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class AuroraFactorsViewModelFactory(
	private val auroraReports: Flowable<AuroraReport>,
	private val darknessChanceEvaluator: ChanceEvaluator<Darkness>,
	private val darknessFormatter: SingleValueFormatter<Darkness>,
	private val geomagLocationChanceEvaluator: ChanceEvaluator<GeomagLocation>,
	private val geomagLocationFormatter: SingleValueFormatter<GeomagLocation>,
	private val kpIndexChanceEvaluator: ChanceEvaluator<KpIndex>,
	private val kpIndexFormatter: SingleValueFormatter<KpIndex>,
	private val visibilityChanceEvaluator: ChanceEvaluator<Visibility>,
	private val visibilityFormatter: SingleValueFormatter<Visibility>
) : ViewModelProvider.Factory {

	@Suppress("UNCHECKED_CAST")
	override fun <T : ViewModel?> create(modelClass: Class<T>): T {
		return when {
			modelClass.isAssignableFrom(DarknessViewModel::class.java) -> {
				DarknessViewModel(
					auroraReports.map { it.factors.darkness },
					darknessFormatter,
					darknessChanceEvaluator
				) as T
			}
			modelClass.isAssignableFrom(GeomagLocationViewModel::class.java) -> {
				GeomagLocationViewModel(
					auroraReports.map { it.factors.geomagLocation },
					geomagLocationFormatter,
					geomagLocationChanceEvaluator
				) as T
			}
			modelClass.isAssignableFrom(KpIndexViewModel::class.java) -> {
				KpIndexViewModel(
					auroraReports.map { it.factors.kpIndex },
					kpIndexFormatter,
					kpIndexChanceEvaluator
				) as T
			}
			modelClass.isAssignableFrom(VisibilityViewModel::class.java) -> {
				VisibilityViewModel(
					auroraReports.map { it.factors.visibility },
					visibilityFormatter,
					visibilityChanceEvaluator
				) as T
			}
			else -> throw IllegalArgumentException("Unsupported ViewModel class: $modelClass")
		}
	}
}
