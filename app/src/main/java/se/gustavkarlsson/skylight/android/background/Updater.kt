package se.gustavkarlsson.skylight.android.background

import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.notifications.NotificationHandler
import se.gustavkarlsson.skylight.android.observers.ObservableValue
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class Updater
@Inject
internal constructor(
		private val context: Context,
		@param:Named(LATEST_NAME) private val latestReportCache: SingletonCache<AuroraReport>,
		private val broadcastManager: LocalBroadcastManager,
		private val notificationHandler: NotificationHandler,
		@param:Named(LATEST_NAME) private val latestAuroraReport: ObservableValue<AuroraReport>
) {

    fun update(timeout: Duration): Boolean {
        Log.v(TAG, "onUpdate")
        val provider = getApplicationComponent().auroraReportProvider
        val report: AuroraReport
        try {
            report = provider.getReport(timeout)
        } catch (e: UserFriendlyException) {
            val errorMessage = context.getString(e.stringResourceId)
            Log.e(TAG, "A user friendly exception occurred: " + errorMessage, e)
            broadcastError(errorMessage)
            return false
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred.", e)
            val errorMessage = context.getString(R.string.error_unknown_update_error)
            broadcastError(errorMessage)
            return false
        }

        latestReportCache.value = report
        latestAuroraReport.value = report
        notificationHandler.handle(report)
        return true
    }

    private fun broadcastError(message: String) {
        val intent = Intent(RESPONSE_UPDATE_ERROR)
		intent.putExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE, message)
        broadcastManager.sendBroadcast(intent)
    }

	// TODO try to get rid of companion object
    companion object {
        private val TAG = Updater::class.java.simpleName
        val RESPONSE_UPDATE_ERROR = TAG + ".RESPONSE_UPDATE_ERROR"
        val RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE = TAG + ".RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE"
    }
}
