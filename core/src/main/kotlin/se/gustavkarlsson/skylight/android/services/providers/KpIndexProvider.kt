package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report

interface KpIndexProvider {
	fun get(): Single<Report<KpIndex>>
	fun stream(): Flowable<Report<KpIndex>>
}
