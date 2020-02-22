package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import de.halfbit.knot.Knot
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.main.evaluation.CompleteAuroraReportEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.DarknessEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.GeomagLocationEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.KpIndexEvaluator
import se.gustavkarlsson.skylight.android.feature.main.evaluation.WeatherEvaluator
import se.gustavkarlsson.skylight.android.feature.main.formatters.ChanceLevelFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.DarknessFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.GeomagLocationFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.KpIndexFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.WeatherFormatter
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.feature.main.gui.drawer.DrawerViewModel
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter

val featureMainModule = module {

    single<RelativeTimeFormatter> {
        val rightNowText = get<Context>().getString(R.string.right_now)
        DateUtilsRelativeTimeFormatter(rightNowText)
    }

    single {
        CombiningAuroraReportProvider(
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

    single<ChanceEvaluator<CompleteAuroraReport>>("completeAuroraReport") {
        CompleteAuroraReportEvaluator(
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

    single {
        ChanceToColorConverter(context = get())
    }

    factory {
        MainViewModel(
            mainKnot = get("main"),
            auroraChanceEvaluator = get("completeAuroraReport"),
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
            permissionChecker = get(),
            chanceToColorConverter = get(),
            time = get(),
            nowTextThreshold = 1.minutes
        )
    }

    factory {
        DrawerViewModel(
            placesRepository = get(),
            selectedPlaceRepo = get()
        )
    }

    single("main") {
        buildMainKnot(
            permissionChecker = get(),
            selectedPlaceRepo = get(),
            auroraReportProvider = get(),
            locationProvider = get()
        )
    }

    single("state") {
        get<Knot<State, Change>>("main").state
    }

    single("change") {
        get<Knot<State, Change>>("main").change
    }
}
