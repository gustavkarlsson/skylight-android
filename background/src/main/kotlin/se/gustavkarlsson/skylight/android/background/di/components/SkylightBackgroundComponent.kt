package se.gustavkarlsson.skylight.android.background.di.components

import android.app.Activity
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.background.di.modules.*
import se.gustavkarlsson.skylight.android.di.modules.*

open class SkylightBackgroundComponent(
	contextModule: ContextModule,
	formattingModule: FormattingModule,
	evaluationModule: EvaluationModule,
	krateModule: KrateModule,
	timeModule: TimeModule,
	activityClass: Class<out Activity>
) : BackgroundComponent {

	open val notificationModule: NotificationModule by lazy {
		AndroidNotificationModule(
			contextModule,
			formattingModule,
			evaluationModule,
			krateModule,
			timeModule,
			notifiedChanceRepositoryModule,
			activityClass
		)
	}

	open val notifiedChanceRepositoryModule: NotifiedChanceRepositoryModule by lazy {
		RoomNotifiedChanceRepositoryModule(contextModule)
	}

	open val schedulingModule: SchedulingModule by lazy {
		EvernoteJobSchedulingModule(
			contextModule,
			krateModule,
			notificationModule
		)
	}

	final override val scheduleBackgroundNotifications: Completable
		get() = schedulingModule.scheduleBackgroundNotifications

}
