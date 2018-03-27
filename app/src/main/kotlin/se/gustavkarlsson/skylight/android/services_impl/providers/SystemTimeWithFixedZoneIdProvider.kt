package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class SystemTimeWithFixedZoneIdProvider(private val zoneId: ZoneId) : TimeProvider {
	override fun getTime(): Single<Instant> = Single.fromCallable { Instant.now() }
	override fun getZoneId(): Single<ZoneId> = Single.just(zoneId)
	override fun getLocalDate(): Single<LocalDate> = Single.fromCallable { LocalDate.now(zoneId) }
}
