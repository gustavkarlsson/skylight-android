package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import de.halfbit.knot.Knot
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.LocationProvider
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository

@Component(
    modules = [
        MainModule::class
    ],
    dependencies = [
        AppComponent::class,
        TimeComponent::class,
        WeatherComponent::class
    ]
)
internal interface MainComponent {
    fun viewModel(): MainViewModel

    companion object {
        fun build(): MainComponent =
            DaggerMainComponent.builder()
                .appComponent(AppComponent.instance)
                .timeComponent(TimeComponent.instance)
                .weatherComponent(WeatherComponent.instance)
                .build()
    }
}

@Module
internal class MainModule {

    @Provides
    fun mainKnot(
        permissionChecker: PermissionChecker,
        selectedPlaceRepository: SelectedPlaceRepository,
        locationProvider: LocationProvider,
        auroraReportProvider: AuroraReportProvider
    ): Knot<State, Change> = buildMainKnot(
        permissionChecker,
        selectedPlaceRepository,
        locationProvider,
        auroraReportProvider
    )

    @Provides
    fun viewModel(
        context: Context,
        mainKnot: Knot<State, Change>,
        time: Time,
        auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
        chanceLevelFormatter: Formatter<ChanceLevel>,
        kpIndexEvaluator: ChanceEvaluator<KpIndex>,
        kpIndexFormatter: Formatter<KpIndex>,
        geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
        geomagLocationFormatter: Formatter<GeomagLocation>,
        weatherEvaluator: ChanceEvaluator<Weather>,
        weatherFormatter: Formatter<Weather>,
        darknessEvaluator: ChanceEvaluator<Darkness>,
        darknessFormatter: Formatter<Darkness>,
        locationPermissionChecker: PermissionChecker
    ): MainViewModel {
        val rightNowText = context.getString(R.string.right_now)
        val relativeTimeFormatter = DateUtilsRelativeTimeFormatter(rightNowText)
        return MainViewModel(
            mainKnot,
            auroraChanceEvaluator,
            relativeTimeFormatter,
            chanceLevelFormatter,
            darknessEvaluator,
            darknessFormatter,
            geomagLocationEvaluator,
            geomagLocationFormatter,
            kpIndexEvaluator,
            kpIndexFormatter,
            weatherEvaluator,
            weatherFormatter,
            locationPermissionChecker,
            ChanceToColorConverter(context),
            time,
            1.minutes
        )
    }
}
