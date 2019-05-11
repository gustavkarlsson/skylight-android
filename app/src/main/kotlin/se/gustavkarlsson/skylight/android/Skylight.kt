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
import se.gustavkarlsson.skylight.android.krate.BootstrapCommand
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.util.CrashlyticsTree
import se.gustavkarlsson.skylight.android.util.KoinTimberLogger
import timber.log.Timber
import timber.log.Timber.DebugTree

class Skylight : MultiDexApplication() {

	private val disposables = CompositeDisposable()

	private val store: SkylightStore by inject()

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
		setupSettingsAnalytics()
		store.issue(BootstrapCommand)
		// FIXME store.issue(SettingsStreamCommand(true))
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
				analytics.setNotificationsEnabled(it.notificationsEnabled)
				analytics.setNotifyTriggerLevel(it.triggerLevel)
			}
			.addTo(disposables)
		*/
	}

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
