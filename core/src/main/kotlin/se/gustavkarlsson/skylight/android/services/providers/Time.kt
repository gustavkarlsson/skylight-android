package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

interface Time {
	fun now(): Single<Instant>
	fun zoneId(): Single<ZoneId>
	fun localDate(): Single<LocalDate>
}
