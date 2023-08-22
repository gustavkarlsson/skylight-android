package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import kotlin.time.TimeSource

internal class NotifyWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val work = BackgroundComponent.instance.backgroundWork()
        logInfo { "Starting work" }
        val startTime = TimeSource.Monotonic.markNow()
        return try {
            work()
            val elapsed = startTime.elapsedNow()
            logInfo { "Finished work in $elapsed" }
            Result.success()
        } catch (e: CancellationException) {
            val elapsed = startTime.elapsedNow()
            logInfo(e) { "Worker cancelled after $elapsed" }
            throw e
        } catch (e: Exception) {
            val elapsed = startTime.elapsedNow()
            logError(e) { "Failed to complete work after $elapsed" }
            Result.failure()
        }
    }
}
