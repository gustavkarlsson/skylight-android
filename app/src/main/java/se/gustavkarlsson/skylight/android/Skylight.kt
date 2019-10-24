package se.gustavkarlsson.skylight.android

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.Settings
import timber.log.Timber
import timber.log.Timber.DebugTree

internal class Skylight : MultiDexApplication() {

	private val disposables = CompositeDisposable()

	override fun onCreate() {
		super.onCreate()
		if (LeakCanary.isInAnalyzerProcess(this)) return
		LeakCanary.install(this)
		AndroidThreeTen.init(this)
		initLogging()
		initRxJavaErrorHandling()
		startKoin(this, modules, logger = KoinTimberLogger)
		initializeModules()
		get<Analytics>().run {
			trackSettings()
			trackPlacesCount()
		}
		scheduleBackgroundNotifications()
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
		// FIXME start background stuff like this too
	}

	private fun Analytics.trackSettings() {
		get<Settings>()
			.notificationTriggerLevels
			.map { it.unzip().second }
			.map { triggerLevels ->
				val min = triggerLevels.minBy { it?.ordinal ?: Int.MAX_VALUE }
				val max = triggerLevels.maxBy { it?.ordinal ?: Int.MAX_VALUE }
				min to max
			}
			.distinctUntilChanged()
			.subscribe { (min, max) ->
				setProperty("notification_trigger_lvl_min", min)
				setProperty("notification_trigger_lvl_max", max)
			}
			.addTo(disposables)
	}

	private fun Analytics.trackPlacesCount() {
		get<PlacesRepository>()
			.all
			.map { it.count() }
			.distinctUntilChanged()
			.subscribe { placesCount ->
				setProperty("places_count", placesCount)
			}
			.addTo(disposables)
	}

	private fun scheduleBackgroundNotifications() {
		get<Completable>("scheduleBackgroundNotifications")
			.subscribe()
			.addTo(disposables)
	}
}
