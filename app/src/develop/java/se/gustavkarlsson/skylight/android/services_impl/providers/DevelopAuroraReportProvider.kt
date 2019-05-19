package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.Time

class DevelopAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val developSettings: DevelopSettings,
	private val time: Time,
	private val pollingInterval: Duration
) : AuroraReportProvider {

	override fun get(location: Location?): Single<AuroraReport> {
		return Single.fromCallable {
			if (developSettings.overrideValues) {
				val timestamp = time.now().blockingGet()
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
				realProvider.get(location)
			}
		}.flatMap { it }

	}

	override fun stream(location: Location?): Flowable<AuroraReport> =
		realProvider.stream(location) // FIXME Honor develop settings
}
