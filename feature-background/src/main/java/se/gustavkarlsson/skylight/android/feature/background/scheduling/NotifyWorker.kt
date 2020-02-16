package se.gustavkarlsson.skylight.android.feature.background.scheduling

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.standalone.KoinComponent
import org.koin.standalone.get
import timber.log.Timber

internal class NotifyWorker(
	appContext: Context,
	workerParams: WorkerParameters
) : RxWorker(appContext, workerParams), KoinComponent {

	override fun createWork(): Single<Result> {
		val work = get<Completable>("notify")
		return work
			.toSingleDefault(Result.success())
			.doOnError { Timber.e(it, "Failed to complete work") }
			.onErrorReturnItem(Result.retry())
	}
}
