package se.gustavkarlsson.skylight.android

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigationsetup.NavigationSetupComponent
import se.gustavkarlsson.skylight.android.lib.permissions.PermissionsComponent
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.settings.SettingsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import se.gustavkarlsson.skylight.android.lib.weather.WeatherComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
internal class Skylight : MultiDexApplication(), AppComponent.Setter, AnalyticsComponent.Setter,
    BackgroundComponent.Setter, TimeComponent.Setter, SettingsComponent.Setter,
    RunVersionComponent.Setter, WeatherComponent.Setter, GeocoderComponent.Setter,
    PlacesComponent.Setter, LocationComponent.Setter, AuroraComponent.Setter,
    PermissionsComponent.Setter, NavigationSetupComponent.Setter, ScopedServiceComponent.Setter {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        AndroidThreeTen.init(this)
        initLogging()
        initRxJavaErrorHandling()
        setupDagger()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        Timber.plant(CrashlyticsTree(Crashlytics.getInstance().core))
    }

    private fun initRxJavaErrorHandling() {
        RxJavaPlugins.setErrorHandler {
            Timber.e(it, "Unhandled RxJava error")
        }
    }

    private fun setupDagger() {
        val component = DaggerActualAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        setAppComponent(component)
        setAnalyticsComponent(component)
        setBackgroundComponent(component)
        setTimeComponent(component)
        setSettingsComponent(component)
        setRunVersionComponent(component)
        setWeatherComponent(component)
        setGeocoderComponent(component)
        setPlacesComponent(component)
        setLocationComponent(component)
        setAuroraComponent(component)
        setPermissionsComponent(component)
        setNavigationSetupComponent(component)
        setScopedServiceComponent(component)

        component.moduleStarters().forEach(ModuleStarter::start)
    }
}
