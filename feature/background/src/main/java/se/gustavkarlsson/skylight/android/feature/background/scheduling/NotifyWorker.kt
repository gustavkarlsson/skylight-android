package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.CancellationException
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent

internal class NotifyWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val work = BackgroundComponent.instance.backgroundWork()
        logInfo { "Starting work" }
        val startTime = System.currentTimeMillis()
        return try {
            work()
            val elapsed = timeSince(startTime)
            logInfo { "Finished work in $elapsed" }
            Result.success()
        } catch (e: CancellationException) {
            val elapsed = timeSince(startTime)
            logInfo(e) { "Worker cancelled after $elapsed" }
            throw e
        } catch (e: Exception) {
            val elapsed = timeSince(startTime)
            logError(e) { "Failed to complete work after $elapsed" }
            Result.failure()
        }
    }
}

private fun timeSince(startTime: Long): Duration? {
    val elapsedMillis = System.currentTimeMillis() - startTime
    return Duration.ofMillis(elapsedMillis)
}
