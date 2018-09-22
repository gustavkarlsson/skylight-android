package se.gustavkarlsson.skylight.android

import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin
import se.gustavkarlsson.skylight.android.krate.BootstrapCommand
import se.gustavkarlsson.skylight.android.krate.SettingsStreamCommand
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.util.CrashlyticsTree
import se.gustavkarlsson.skylight.android.util.KoinTimberLogger
import timber.log.Timber
import timber.log.Timber.DebugTree

class Skylight : MultiDexApplication() {

	private val disposables = CompositeDisposable()

	private val store: SkylightStore by inject()

	init {
		instance = this
	}

	override fun onCreate() {
		super.onCreate()
		if (LeakCanary.isInAnalyzerProcess(this)) return
		LeakCanary.install(this)
		initCrashReporting()
		initLogging()
		AndroidThreeTen.init(this)
		initRxJavaErrorHandling()
		startKoin(this, modules, logger = KoinTimberLogger())
		initAnalytics()
		setupSettingsAnalytics()
		store.issue(BootstrapCommand)
		store.issue(SettingsStreamCommand(true))
		scheduleBackgroundNotifications()
	}

	private fun initCrashReporting() {
		if (!BuildConfig.DEBUG) {
			Fabric.with(this, Crashlytics())
		}
	}

	private fun initLogging() {
		if (BuildConfig.DEBUG) {
			Timber.plant(DebugTree())
		} else {
			Timber.plant(CrashlyticsTree(Crashlytics.getInstance().core))
		}
	}

	private fun initAnalytics() {
		Analytics.instance = get()
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
		store.states
			.map { it.settings }
			.distinctUntilChanged()
			.subscribe {
				Analytics.setNotificationsEnabled(it.notificationsEnabled)
				Analytics.setNotifyTriggerLevel(it.triggerLevel)
			}
			.addTo(disposables)
	}

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
