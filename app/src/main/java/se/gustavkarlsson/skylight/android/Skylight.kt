package se.gustavkarlsson.skylight.android

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.reactivex.plugins.RxJavaPlugins
import org.koin.android.ext.android.get
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import timber.log.Timber.DebugTree

@Suppress("unused")
internal class Skylight : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) return
        LeakCanary.install(this)
        AndroidThreeTen.init(this)
        initLogging()
        initRxJavaErrorHandling()
        startKoin(this, modules, logger = KoinTimberLogger)
        initializeModules()
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

    private fun initializeModules() {
        get<ModuleStarter>("intro").start()
        get<ModuleStarter>("main").start()
        get<ModuleStarter>("googleplayservices").start()
        get<ModuleStarter>("about").start()
        get<ModuleStarter>("addplace").start()
        get<ModuleStarter>("settings").start()
        get<ModuleStarter>("background").start()
        get<ModuleStarter>("places").start()
    }
}
