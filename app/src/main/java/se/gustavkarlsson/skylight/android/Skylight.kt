package se.gustavkarlsson.skylight.android

import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.aurora.AuroraComponent
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessComponent
import se.gustavkarlsson.skylight.android.lib.geocoder.GeocoderComponent
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationComponent
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexComponent
import se.gustavkarlsson.skylight.android.lib.location.LocationComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
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
internal class Skylight : Application(), AppComponent.Setter, AnalyticsComponent.Setter,
    BackgroundComponent.Setter, TimeComponent.Setter, SettingsComponent.Setter,
    RunVersionComponent.Setter, WeatherComponent.Setter, GeocoderComponent.Setter,
    PlacesComponent.Setter, LocationComponent.Setter, AuroraComponent.Setter,
    PermissionsComponent.Setter, NavigationSetupComponent.Setter, ScopedServiceComponent.Setter,
    NavigationComponent.Setter, DarknessComponent.Setter, KpIndexComponent.Setter,
    GeomagLocationComponent.Setter {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        AndroidThreeTen.init(this)
        initLogging()
        initRxJavaErrorHandling()
        setupDagger()
        initStrictMode()
    }

    private fun initStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().apply {
                    detectCustomSlowCalls()
                    detectDiskReads()
                    detectDiskWrites()
                    detectNetwork()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        detectResourceMismatches()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectUnbufferedIo()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        penaltyListener(Runnable::run, Timber::e)
                    } else {
                        penaltyLog()
                    }
                }.build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder().apply {
                    detectActivityLeaks()
                    detectFileUriExposure()
                    detectLeakedClosableObjects()
                    detectLeakedRegistrationObjects()
                    detectLeakedSqlLiteObjects()
                    // Ignored for now since AppCompatDelegateImpl.computeFitSystemWindows calls this
                    // detectNonSdkApiUsage()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        detectCleartextNetwork()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectContentUriWithoutPermission()
                        // Ignored because OkHttp doesn't deal with this
                        // https://github.com/square/okhttp/issues/3537#issuecomment-619414434
                        // detectUntaggedSockets()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        detectCredentialProtectedWhileLocked()
                        detectImplicitDirectBoot()
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        penaltyListener(Runnable::run, Timber::e)
                    } else {
                        penaltyLog()
                    }
                }.build()
            )
        }
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
        Timber.plant(CrashlyticsTree(FirebaseCrashlytics.getInstance()))
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
        setNavigationComponent(component)
        setDarknessComponent(component)
        setKpIndexComponent(component)
        setGeomagLocationComponent(component)

        component.moduleStarters().forEach(ModuleStarter::start)
    }
}
