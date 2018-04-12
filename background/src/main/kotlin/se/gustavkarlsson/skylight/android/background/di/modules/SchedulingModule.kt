package se.gustavkarlsson.skylight.android.background.di.modules

import io.reactivex.Completable

interface SchedulingModule {
	val scheduleBackgroundNotifications: Completable
}
