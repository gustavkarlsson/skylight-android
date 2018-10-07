package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.Time

class DevelopAuroraReportStreamable(
	private val realStreamable: Streamable<AuroraReport>,
	private val developSettings: DevelopSettings,
	private val time: Time,
	private val pollingInterval: Duration
) : Streamable<AuroraReport> {

	override val stream: Flowable<AuroraReport>
		get() = developSettings.overrideValuesChanges
			.switchMap { enabled ->
				if (enabled) {
					Single.fromCallable {
						val timestamp = time.now().blockingGet()
						developSettings.run {
							AuroraReport(
								"Fake Location",
								Report.success(KpIndex(kpIndex), timestamp),
								Report.success(GeomagLocation(geomagLatitude), timestamp),
								Report.success(Darkness(sunZenithAngle), timestamp),
								Report.success(Weather(cloudPercentage), timestamp)
							)
						}
					}.repeatWhen { it.delay(pollingInterval) }
				} else {
					realStreamable.stream
				}
			}
}
