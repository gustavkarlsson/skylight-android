package se.gustavkarlsson.skylight.android

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.dagger.modules.VisibilityModule
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob
import se.gustavkarlsson.skylight.android.util.CrashlyticsTree
import timber.log.Timber
import timber.log.Timber.DebugTree





class Skylight : MultiDexApplication() {

	lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
		instance = this
		component = DaggerApplicationComponent.builder()
			.contextModule(ContextModule(this))
			.visibilityModule(VisibilityModule(BuildConfig.OPENWEATHERMAP_API_KEY))
			.build()
		bootstrap()
		setupNotifications()
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
			Timber.w(it, "Unhandled RxJava error")
		}
	}

	private fun initJobManager() {
		JobManager.create(this).run {
			addJobCreator { tag ->
				when (tag) {
					UpdateJob.UPDATE_JOB_TAG -> component.getUpdateJob()
					else -> null
				}
			}
		}
	}

	private fun setupNotifications() {
		val settings = component.getSettings()
		val scheduler = component.getScheduler()
		settings.notificationsEnabledChanges
			.subscribe { enabled ->
				if (enabled) {
					scheduler.schedule()
				} else {
					scheduler.unschedule()
				}
			}
	}

	companion object {
		lateinit var instance: Skylight
	}

}
