package se.gustavkarlsson.skylight.android

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
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
		setupNotifications()
		setupSettingsAnalytics(appModule.settings)
	}

	private fun bootstrap() {
		setupCrashReporting()
		setupLogging()
		AndroidThreeTen.init(this)
		setupRxJavaErrorHandling()
		initJobManager()
	}

	private fun setupCrashReporting() {
		val crashlytics = Crashlytics.Builder()
			.core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
			.build()

		Fabric.with(this, crashlytics)
	}

	private fun setupLogging() {
		if (BuildConfig.DEBUG) {
			Timber.plant(DebugTree())
		} else {
			Timber.plant(CrashlyticsTree(Crashlytics.getInstance().core))
		}
	}

	private fun setupRxJavaErrorHandling() {
		RxJavaPlugins.setErrorHandler {
			Timber.e(it, "Unhandled RxJava error")
		}
	}

	private fun initJobManager() {
		JobManager.create(this).run {
			addJobCreator { tag ->
				when (tag) {
					UpdateJob.UPDATE_JOB_TAG -> appModule.updateJob
					else -> null
				}
			}
		}
	}

	private fun setupNotifications() {
		val settings = appModule.settings
		val scheduler = appModule.updateScheduler
		settings.notificationsEnabledChanges
			.subscribe { enabled ->
				if (enabled) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
	}

	private fun setupSettingsAnalytics(settings: Settings) {
		settings.notificationsEnabledChanges
			.subscribe(analytics::setNotificationsEnabled)

		settings.triggerLevelChanges
			.subscribe(analytics::setNotifyTriggerLevel)
	}

	companion object {
		lateinit var instance: Skylight
			private set
	}

}
