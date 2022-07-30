package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.utils.minutes
import java.util.concurrent.TimeUnit
import javax.inject.Inject

internal class NotifyScheduler(
    private val appContext: Context,
    private val scheduleInterval: Duration,
) : Scheduler {

    @Inject
    constructor(context: Context) : this(
        appContext = context,
        scheduleInterval = 15.minutes,
    )

    private val workManager get() = WorkManager.getInstance(appContext)

    override fun schedule() {
        val request = createRequest(scheduleInterval)
        workManager.enqueueUniquePeriodicWork(
            UNIQUE_NAME_NOTIFY,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
        logDebug { "Scheduled periodic updates" }
    }

    override fun unschedule() {
        workManager.cancelUniqueWork(UNIQUE_NAME_NOTIFY)
        logDebug { "Unscheduled periodic updates" }
    }
}

private const val UNIQUE_NAME_NOTIFY = "NOTIFY"

internal fun createRequest(interval: Duration) =
    PeriodicWorkRequestBuilder<NotifyWorker>(interval.toMillis(), TimeUnit.MILLISECONDS)
        .setInitialDelay(interval.toMillis(), TimeUnit.MILLISECONDS)
        .setConstraints(buildConstraints())
        .build()

private fun buildConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()
