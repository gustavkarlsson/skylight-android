package se.gustavkarlsson.skylight.android.services.providers

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId

interface TimeProvider {
	fun getTime(): Single<Instant>
	fun getZoneId(): Single<ZoneId>
	fun getLocalDate(): Single<LocalDate>
}
