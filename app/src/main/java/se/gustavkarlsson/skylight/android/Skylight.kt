package se.gustavkarlsson.skylight.android

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
internal class Skylight : MultiDexApplication(), AppComponent.Setter {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
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
        component.moduleStarters().forEach(ModuleStarter::start)
        setAppComponent(component)
    }
}
