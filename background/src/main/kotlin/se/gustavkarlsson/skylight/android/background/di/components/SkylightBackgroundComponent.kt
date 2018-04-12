package se.gustavkarlsson.skylight.android.background.di.components

import android.app.Activity
import io.reactivex.Completable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.di.modules.*
import se.gustavkarlsson.skylight.android.di.modules.*

open class SkylightBackgroundComponent(
	contextModule: ContextModule,
	formattingModule: FormattingModule,
	evaluationModule: EvaluationModule,
	settingsModule: SettingsModule,
	auroraReportModule: AuroraReportModule,
	timeModule: TimeModule,
	activityClass: Class<out Activity>,
	scheduleInterval: Duration,
	schedulerFlex: Duration
) : BackgroundComponent {

	open val notificationModule: NotificationModule by lazy {
		AndroidNotificationModule(
			contextModule,
			formattingModule,
			evaluationModule,
			settingsModule,
			timeModule,
			notifiedRepositoryModule,
			activityClass
		)
	}

	open val notifiedRepositoryModule: NotifiedRepositoryModule by lazy {
		RoomNotifiedRepositoryModule(
			contextModule
		)
	}

	open val schedulingModule: SchedulingModule by lazy {
		EvernoteJobSchedulingModule(
			contextModule,
			auroraReportModule,
			notificationModule,
			settingsModule,
			scheduleInterval,
			schedulerFlex
		)
	}

	final override val scheduleBackgroundNotifications: Completable
		get() = schedulingModule.scheduleBackgroundNotifications

}
