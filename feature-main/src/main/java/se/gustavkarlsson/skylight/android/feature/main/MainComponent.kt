package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import dagger.Component
import dagger.Module
import dagger.Provides
import de.halfbit.knot.Knot
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.feature.main.formatters.DarknessFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.GeomagLocationFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.KpIndexFormatter
import se.gustavkarlsson.skylight.android.feature.main.formatters.WeatherFormatter
import se.gustavkarlsson.skylight.android.feature.main.gui.MainViewModel
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.LocationProvider
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.services.Time
import java.util.Locale

@Component(
    modules = [MainModule::class],
    dependencies = [AppComponent::class]
)
internal interface MainComponent {
    fun viewModel(): MainViewModel

    companion object {
        fun viewModel(): MainViewModel =
            DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .viewModel()
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
        locale: Single<Locale>,
        time: Time,
        auroraChanceEvaluator: ChanceEvaluator<CompleteAuroraReport>,
        chanceLevelFormatter: Formatter<ChanceLevel>,
        kpIndexEvaluator: ChanceEvaluator<KpIndex>,
        geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
        weatherEvaluator: ChanceEvaluator<Weather>,
        darknessEvaluator: ChanceEvaluator<Darkness>,
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
            DarknessFormatter,
            geomagLocationEvaluator,
            GeomagLocationFormatter(locale),
            kpIndexEvaluator,
            KpIndexFormatter,
            weatherEvaluator,
            WeatherFormatter,
            locationPermissionChecker,
            ChanceToColorConverter(context),
            time,
            1.minutes
        )
    }
}
