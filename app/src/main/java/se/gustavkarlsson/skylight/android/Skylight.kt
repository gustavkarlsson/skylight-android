package se.gustavkarlsson.skylight.android

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import se.gustavkarlsson.skylight.android.lib.analytics.AnalyticsComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
internal class Skylight : MultiDexApplication(), AppComponent.Setter, AnalyticsComponent.Setter,
    BackgroundComponent.Setter, TimeComponent.Setter {

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
        component.moduleStarters().forEach(ModuleStarter::start)
    }
}
