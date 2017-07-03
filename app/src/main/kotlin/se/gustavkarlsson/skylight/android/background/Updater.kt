package se.gustavkarlsson.skylight.android.background

import android.content.Context
import dagger.Reusable
import io.reactivex.subjects.Subject
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight.Companion.applicationComponent
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.notifications.NotificationHandler
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class Updater
@Inject
constructor(
		private val context: Context,
		private val notificationHandler: NotificationHandler,
		@param:Named(LATEST_NAME) private val latestAuroraReportSubject: Subject<AuroraReport>,
		private val userFriendlyExceptionSubject: Subject<UserFriendlyException>
) : AnkoLogger {

	fun update(timeout: Duration): Boolean {
		val provider = applicationComponent.getAuroraReportProvider()
		try {
			val report = provider.getReport(timeout)
			latestAuroraReportSubject.onNext(report)
			notificationHandler.handle(report)
			return true
		} catch (e: UserFriendlyException) {
			val errorMessage = context.getString(e.stringResourceId)
			error("A user friendly exception occurred: $errorMessage", e)
			userFriendlyExceptionSubject.onNext(e)
			return false
		} catch (e: Exception) {
			error("An unexpected error occurred", e)
			userFriendlyExceptionSubject.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
			return false
		}
	}
}
