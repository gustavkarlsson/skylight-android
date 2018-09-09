package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.evaluation.*

val evaluationModule = module {

	single<ChanceEvaluator<KpIndex>>("kpIndex") {
		KpIndexEvaluator()
	}

	single<ChanceEvaluator<GeomagLocation>>("geomagLocation") {
		GeomagLocationEvaluator()
	}

	single<ChanceEvaluator<Weather>>("weather") {
		WeatherEvaluator()
	}

	single<ChanceEvaluator<Darkness>>("darkness") {
		DarknessEvaluator()
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
