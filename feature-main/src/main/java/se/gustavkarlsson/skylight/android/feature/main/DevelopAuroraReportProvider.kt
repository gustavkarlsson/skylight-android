package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.lib.settings.DevelopSettings
import se.gustavkarlsson.skylight.android.services.Time

internal class DevelopAuroraReportProvider(
	private val realProvider: AuroraReportProvider,
	private val developSettings: DevelopSettings,
	private val time: Time,
	private val pollingInterval: Duration
) : AuroraReportProvider {

	override fun get(location: Single<LocationResult>): Single<CompleteAuroraReport> =
		Single.fromCallable {
			if (developSettings.overrideValues) {
				val timestamp = time.now().blockingGet()
				val auroraReport = developSettings.run {
					CompleteAuroraReport(
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

	override fun stream(
		locations: Flowable<Loadable<LocationResult>>
	): Flowable<LoadableAuroraReport> =
		realProvider.stream(locations) // FIXME Honor develop settings
}
