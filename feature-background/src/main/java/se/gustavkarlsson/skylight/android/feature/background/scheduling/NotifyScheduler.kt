package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import org.threeten.bp.Duration
import timber.log.Timber

internal class NotifyScheduler(
    private val appContext: Context,
    private val scheduleInterval: Duration
) : Scheduler {

    private val workManager get() = WorkManager.getInstance(appContext)

    override fun schedule() {
        val request = createRequest(scheduleInterval)
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_NAME_NOTIFY,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
        Timber.d("Scheduled periodic updates")
    }

    override fun unschedule() {
        workManager.cancelUniqueWork(UNIQUE_NAME_NOTIFY)
        Timber.d("Unscheduled periodic updates")
    }
}

private const val UNIQUE_NAME_NOTIFY = "NOTIFY"

internal fun createRequest(interval: Duration) =
    PeriodicWorkRequestBuilder<NotifyWorker>(interval.toMillis(), TimeUnit.MILLISECONDS)
        .setInitialDelay(interval.toMillis(), TimeUnit.MILLISECONDS)
        .setConstraints(buildConstraints())
        .build()

private fun buildConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED) // TODO Make roaming configurable?
    .setRequiresBatteryNotLow(true)
    .build()
