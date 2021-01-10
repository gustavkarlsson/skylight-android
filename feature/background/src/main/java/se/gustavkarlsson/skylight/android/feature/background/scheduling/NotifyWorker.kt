package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent

internal class NotifyWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // TODO Look into creating factory
        val work = BackgroundComponent.instance.backgroundWork()
        return try {
            work()
            Result.success()
        } catch (e: Exception) {
            logError(e) { "Failed to complete work" }
            Result.retry()
        }
    }
}
