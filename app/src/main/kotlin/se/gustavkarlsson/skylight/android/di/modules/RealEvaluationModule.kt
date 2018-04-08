package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.*

class RealEvaluationModule : EvaluationModule {
	override val kpIndexEvaluator: ChanceEvaluator<KpIndex> by lazy {
		KpIndexEvaluator()
	}

	override val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation> by lazy {
		GeomagLocationEvaluator()
	}

	override val visibilityEvaluator: ChanceEvaluator<Visibility> by lazy {
		VisibilityEvaluator()
	}

	override val darknessEvaluator: ChanceEvaluator<Darkness> by lazy {
		DarknessEvaluator()
	}

	override val auroraReportEvaluator: ChanceEvaluator<AuroraReport> by lazy {
		AuroraReportEvaluator(
			kpIndexEvaluator,
			geomagLocationEvaluator,
			visibilityEvaluator,
			darknessEvaluator
		)
	}
}
