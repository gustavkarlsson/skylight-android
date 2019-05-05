package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.AuroraReportEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.DarknessEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.GeomagLocationEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.KpIndexEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.WeatherEvaluator

val evaluationModule = module {

	single<ChanceEvaluator<KpIndex>>("kpIndex") {
		KpIndexEvaluator
	}

	single<ChanceEvaluator<GeomagLocation>>("geomagLocation") {
		GeomagLocationEvaluator
	}

	single<ChanceEvaluator<Weather>>("weather") {
		WeatherEvaluator
	}

	single<ChanceEvaluator<Darkness>>("darkness") {
		DarknessEvaluator
	}

	single<ChanceEvaluator<AuroraReport>>("auroraReport") {
		AuroraReportEvaluator(
			get("kpIndex"),
			get("geomagLocation"),
			get("weather"),
			get("darkness")
		)
	}

}
