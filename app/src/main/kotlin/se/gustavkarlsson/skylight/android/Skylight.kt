package se.gustavkarlsson.skylight.android

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.util.CrashlyticsTree
import timber.log.Timber
import timber.log.Timber.DebugTree

class Skylight : MultiDexApplication() {
	private val disposables = CompositeDisposable()

	init {
		instance = this
	}

	override fun onCreate() {
		super.onCreate()
		if (LeakCanary.isInAnalyzerProcess(this)) return
		bootstrap()
		appComponent.store.start()
		setupSettingsAnalytics(appComponent.settings)
		scheduleBackgroundNotifications()
	}

	private fun bootstrap() {
		LeakCanary.install(this)
		initCrashReporting()
		initLogging()
		initAnalytics()
		AndroidThreeTen.init(this)
		initRxJavaErrorHandling()
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
		Analytics.instance = appComponent.analytics
	}

	private fun initRxJavaErrorHandling() {
		RxJavaPlugins.setErrorHandler {
			Timber.e(it, "Unhandled RxJava error")
		}
	}

	private fun scheduleBackgroundNotifications() {
		appComponent.backgroundComponent.scheduleBackgroundNotifications
			.subscribe()
	}

	private fun setupSettingsAnalytics(settings: Settings) {
		settings.notificationsEnabledChanges
			.subscribe { Analytics.setNotificationsEnabled(it) }
			.addTo(disposables)

		settings.triggerLevelChanges
			.subscribe { Analytics.setNotifyTriggerLevel(it) }
			.addTo(disposables)
	}

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
