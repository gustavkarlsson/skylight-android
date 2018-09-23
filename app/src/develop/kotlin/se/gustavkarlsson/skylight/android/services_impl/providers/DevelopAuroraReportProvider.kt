package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

class DevelopAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val developSettings: DevelopSettings,
	private val timeProvider: TimeProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			if (developSettings.overrideValues) {
				val timestamp = timeProvider.getTime().blockingGet()
				val auroraReport = developSettings.run {
					AuroraReport(
						"Fake Location",
						Report.success(KpIndex(kpIndex), timestamp),
						Report.success(GeomagLocation(geomagLatitude), timestamp),
						Report.success(Darkness(sunZenithAngle), timestamp),
						Report.success(Weather(cloudPercentage), timestamp)
					)
				}
				Single.just(auroraReport).delay(developSettings.refreshDuration)
			} else {
				realProvider.get()
			}
		}.flatMap { it }

	}
}
