package se.gustavkarlsson.skylight.android.lib.kpindex

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report

interface KpIndexProvider {
	fun get(): Single<Report<KpIndex>>
	fun stream(): Flowable<Loadable<Report<KpIndex>>>
}
