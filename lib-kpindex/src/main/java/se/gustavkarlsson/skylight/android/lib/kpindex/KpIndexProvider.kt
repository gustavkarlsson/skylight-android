package se.gustavkarlsson.skylight.android.lib.kpindex

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report

interface KpIndexProvider {
	fun get(): Single<Report<KpIndex>>
	val stream: Flowable<Report<KpIndex>>
}
