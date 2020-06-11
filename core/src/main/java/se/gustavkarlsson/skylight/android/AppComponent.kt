package se.gustavkarlsson.skylight.android

import android.content.Context
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.services.*
import java.util.Locale
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    fun locationPermissionRequester(): PermissionRequester

    fun singleLocale(): Single<Locale>

    fun completeAuroraReportChanceEvaluator(): ChanceEvaluator<CompleteAuroraReport>

    fun kpIndexChanceEvaluator(): ChanceEvaluator<KpIndex>

    fun kpIndexFormatter(): Formatter<KpIndex>

    fun weatherChanceEvaluator(): ChanceEvaluator<Weather>

    fun weatherFormatter(): Formatter<Weather>

    fun geomagLocationChanceEvaluator(): ChanceEvaluator<GeomagLocation>

    fun geomagLocationFormatter(): Formatter<GeomagLocation>

    fun darknessChanceEvaluator(): ChanceEvaluator<Darkness>

    fun darknessFormatter(): Formatter<Darkness>

    fun chanceLevelFormatter(): Formatter<ChanceLevel>

    fun locationPermissionChecker(): PermissionChecker

    fun selectedPlaceRepository(): SelectedPlaceRepository

    fun locationProvider(): LocationProvider

    fun auroraReportProvider(): AuroraReportProvider

    fun navigationInstaller(): NavigationInstaller

    fun serviceRegistry(): ServiceRegistry

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

    fun placesRepository(): PlacesRepository

    fun geocoder(): Geocoder

    fun runVersionManager(): RunVersionManager

    fun navigationOverrides(): Set<NavigationOverride>

    fun moduleStarters(): Set<ModuleStarter>

    interface Setter {
        fun setAppComponent(component: AppComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AppComponent
            internal set
    }
}
