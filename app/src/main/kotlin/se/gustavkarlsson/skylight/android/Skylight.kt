package se.gustavkarlsson.skylight.android

import android.support.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.addTo
import se.gustavkarlsson.skylight.android.krate.SettingsStreamCommand
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.Analytics
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
		setupSettingsAnalytics(appComponent.store)
		appComponent.store.issue(SettingsStreamCommand(true))
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
			.addTo(disposables)
	}

	private fun setupSettingsAnalytics(store: SkylightStore) {
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
