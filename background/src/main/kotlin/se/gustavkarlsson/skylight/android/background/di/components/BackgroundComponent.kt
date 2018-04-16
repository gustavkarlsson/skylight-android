package se.gustavkarlsson.skylight.android.background.di.components

import io.reactivex.Completable

interface BackgroundComponent {
	val scheduleBackgroundNotifications: Completable
}
