package se.gustavkarlsson.skylight.android

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.Geocoder
import se.gustavkarlsson.skylight.android.services.LocationProvider
import se.gustavkarlsson.skylight.android.services.NavigationInstaller
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PermissionRequester
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import se.gustavkarlsson.skylight.android.services.ServiceRegistry
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.Time
import java.util.Locale
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    fun time(): Time

    fun analytics(): Analytics

    fun settings(): Settings

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

    @Named("notify")
    fun notifyWork(): Completable

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
