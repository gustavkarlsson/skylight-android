package se.gustavkarlsson.skylight.android

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.flux.GetAuroraReportAction
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.util.CrashlyticsTree
import timber.log.Timber
import timber.log.Timber.DebugTree

class Skylight : MultiDexApplication() {

	init {
		instance = this
	}

	override fun onCreate() {
		super.onCreate()
		bootstrap()
		setupSettingsAnalytics(appComponent.settings)
		scheduleBackgroundNotifications()
		appComponent.store.start()
		appComponent.store.postAction(GetAuroraReportAction)
	}

	private fun bootstrap() {
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

		settings.triggerLevelChanges
			.subscribe { Analytics.setNotifyTriggerLevel(it) }
	}

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
