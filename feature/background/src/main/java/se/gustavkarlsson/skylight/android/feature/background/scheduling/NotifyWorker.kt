package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.feature.background.BackgroundComponent
import timber.log.Timber

internal class NotifyWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : RxWorker(appContext, workerParams) {

    override fun createWork(): Single<Result> {
        // TODO Look into creating factory
        val work = BackgroundComponent.instance.notifyWork()
        return work
            .toSingleDefault(Result.success())
            .doOnError { Timber.e(it, "Failed to complete work") }
            .onErrorReturnItem(Result.retry())
    }
}