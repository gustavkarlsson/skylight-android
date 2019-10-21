package se.gustavkarlsson.skylight.android.services

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

// TODO Introduce blocking versions
interface Time {
	fun now(): Single<Instant>
	fun zoneId(): Single<ZoneId>
	fun localDate(): Single<LocalDate>
}
