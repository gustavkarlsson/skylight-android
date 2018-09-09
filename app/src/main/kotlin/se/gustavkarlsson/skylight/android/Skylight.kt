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
import org.koin.android.ext.android.startKoin
import se.gustavkarlsson.skylight.android.background.backgroundModule
import se.gustavkarlsson.skylight.android.krate.BootstrapCommand
import se.gustavkarlsson.skylight.android.krate.SettingsStreamCommand
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
		startKoin(
			this,
			listOf(
				settingsModule,
				backgroundModule,
				connectivityModule,
				krateModule,
				runVersionsModule,
				googlePlayServicesModule,
				permissionsModule,
				auroraReportModule,
				timeModule,
				locationModule,
				locationNameModule,
				darknessModule,
				geomagLocationModule,
				kpIndexModule,
				weatherModule
			)
		) // TODO Add Timber logger for Koin
		setupSettingsAnalytics()
		appComponent.store.issue(BootstrapCommand)
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
		get<Completable>("scheduleBackgroundNotifications")
			.subscribe()
			.addTo(disposables)
	}

	private fun setupSettingsAnalytics() {
		appComponent.store.states
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
