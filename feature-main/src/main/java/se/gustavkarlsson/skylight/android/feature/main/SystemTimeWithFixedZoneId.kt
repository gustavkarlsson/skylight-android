package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.Time

internal class SystemTimeWithFixedZoneId(private val zoneId: ZoneId) : Time {
	override fun now(): Single<Instant> = Single.fromCallable { Instant.now() }
	override fun zoneId(): Single<ZoneId> = Single.just(zoneId)
	override fun localDate(): Single<LocalDate> = Single.fromCallable { LocalDate.now(zoneId) }
}
