package se.gustavkarlsson.skylight.android

import android.content.Context
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository
import java.util.Locale
import javax.inject.Named

interface AppComponent {

    fun context(): Context

    fun singleLocale(): Single<Locale>

    fun completeAuroraReportChanceEvaluator(): ChanceEvaluator<CompleteAuroraReport>

    fun kpIndexChanceEvaluator(): ChanceEvaluator<KpIndex>

    fun kpIndexFormatter(): Formatter<KpIndex>

    fun geomagLocationChanceEvaluator(): ChanceEvaluator<GeomagLocation>

    fun geomagLocationFormatter(): Formatter<GeomagLocation>

    fun darknessChanceEvaluator(): ChanceEvaluator<Darkness>

    fun darknessFormatter(): Formatter<Darkness>

    fun chanceLevelFormatter(): Formatter<ChanceLevel>

    fun selectedPlaceRepository(): SelectedPlaceRepository

    @Named("versionCode")
    fun versionCode(): Int

    @Named("versionName")
    fun versionName(): String

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
