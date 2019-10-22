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
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import timber.log.Timber
import timber.log.Timber.DebugTree

internal class Skylight : MultiDexApplication() {

	private val disposables = CompositeDisposable()

	private val analytics: Analytics by inject()

	init {
		instance = this
	}

	override fun onCreate() {
		super.onCreate()
		if (LeakCanary.isInAnalyzerProcess(this)) return
		LeakCanary.install(this)
		initLogging()
		AndroidThreeTen.init(this)
		initRxJavaErrorHandling()
		startKoin(this, modules, logger = KoinTimberLogger)
		initializeModules()
		setupSettingsAnalytics()
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

	private fun scheduleBackgroundNotifications() {
		get<Completable>("scheduleBackgroundNotifications")
			.subscribe()
			.addTo(disposables)
	}

	private fun setupSettingsAnalytics() {
		/*
		FIXME
		store.states
			.map { it.settings }
			.distinctUntilChanged()
			.subscribe {
				analytics.setProperty("notifications_enabled", it.notificationsEnabled)
				analytics.setProperty("notifications_level", it.triggerLevel.name)
			}
			.addTo(disposables)
		*/
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

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
