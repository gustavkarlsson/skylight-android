package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import com.tbruyelle.rxpermissions2.RxPermissions
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.ModuleStarter
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.main.evaluation.AuroraReportEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.DarknessEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.GeomagLocationEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.KpIndexEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.WeatherEvaluator
import se.gustavkarlsson.skylight.android.feature.main.formatters.ChanceLevelFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.DarknessFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.GeomagLocationFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.KpIndexFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.WeatherFormatter
import se.gustavkarlsson.skylight.android.feature.main.gui.MainFragment
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.feature.main.gui.drawer.DrawerViewModel
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.lib.places.PlacesRepository
import se.gustavkarlsson.skylight.android.lib.ui.Destination
import se.gustavkarlsson.skylight.android.lib.ui.DestinationRegistry
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter

val mainModule = module {

	single<RelativeTimeFormatter> {
		val rightNowText = get<Context>().getString(R.string.right_now)
		DateUtilsRelativeTimeFormatter(rightNowText)
	}

	single<ModuleStarter>("main") {
		object : ModuleStarter {
			override fun start() {
				val destination = Destination("main", 0, true) { id ->
					if (id == "main")
						MainFragment()
					else
						null
				}
				get<DestinationRegistry>().register(destination)
			}
		}
	}

	single {
		CombiningAuroraReportProvider(
			locationProvider = get(),
			reverseGeocoder = get(),
			darknessProvider = get(),
			geomagLocationProvider = get(),
			kpIndexProvider = get(),
			weatherProvider = get()
		)
	}

	single {
		DevelopAuroraReportProvider(
			realProvider = get<CombiningAuroraReportProvider>(),
			developSettings = get(),
			time = get(),
			pollingInterval = 1.minutes
		)
	}

	single {
		@Suppress("ConstantConditionIf")
		if (BuildConfig.DEVELOP) {
			get<DevelopAuroraReportProvider>()
		} else {
			get<CombiningAuroraReportProvider>()
		}
	}

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
			kpIndexEvaluator = get("kpIndex"),
			geomagLocationEvaluator = get("geomagLocation"),
			weatherEvaluator = get("weather"),
			darknessEvaluator = get("darkness")
		)
	}

	single<Formatter<Darkness>>("darkness") {
		DarknessFormatter
	}

	single<Formatter<GeomagLocation>>("geomagLocation") {
		GeomagLocationFormatter(get("locale"))
	}

	single<Formatter<KpIndex>>("kpIndex") {
		KpIndexFormatter
	}

	single<Formatter<Weather>>("weather") {
		WeatherFormatter
	}

	single<Formatter<ChanceLevel>>("chanceLevel") {
		ChanceLevelFormatter
	}

	single<PermissionChecker> {
		AndroidPermissionChecker(
			context = get(),
			permissionKey = get("locationPermission")
		)
	}

	scope("activity") {
		RxPermissions(get()).apply { setLogging(BuildConfig.DEBUG) }
	}

	single {
		ChanceToColorConverter(context = get())
	}

	viewModel {
		MainViewModel(
			store = get("main"),
			auroraChanceEvaluator = get("auroraReport"),
			relativeTimeFormatter = get(),
			chanceLevelFormatter = get("chanceLevel"),
			darknessChanceEvaluator = get("darkness"),
			darknessFormatter = get("darkness"),
			geomagLocationChanceEvaluator = get("geomagLocation"),
			geomagLocationFormatter = get("geomagLocation"),
			kpIndexChanceEvaluator = get("kpIndex"),
			kpIndexFormatter = get("kpIndex"),
			weatherChanceEvaluator = get("weather"),
			weatherFormatter = get("weather"),
			chanceToColorConverter = get(),
			time = get(),
			nowTextThreshold = 1.minutes
		)
	}

	viewModel {
		DrawerViewModel(store = get("main"), navigator = get())
	}

	single("main") {
		val permissionChecker = get<PermissionChecker>()
		val auroraReportProvider = get<AuroraReportProvider>()
		val placesRepo = get<PlacesRepository>()

		buildSkylightStore(
			permissionChecker,
			placesRepo,
			auroraReportProvider
		)
	}

	single("state") {
		get<SkylightStore>("main").states
	}

}
