package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import me.tatarka.inject.annotations.Inject
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

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
    PeriodicWorkRequestBuilder<NotifyWorker>(interval.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        .setInitialDelay(interval.inWholeMilliseconds, TimeUnit.MILLISECONDS)
        .setConstraints(buildConstraints())
        .build()

private fun buildConstraints() = Constraints.Builder()
    .setRequiredNetworkType(NetworkType.CONNECTED)
    .setRequiresBatteryNotLow(true)
    .build()
