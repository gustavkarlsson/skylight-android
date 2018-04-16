package se.gustavkarlsson.skylight.android.di.modules

import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

interface FormattingModule {
	val relativeTimeFormatter: RelativeTimeFormatter
	val darknessFormatter: SingleValueFormatter<Darkness>
	val geomagLocationFormatter: SingleValueFormatter<GeomagLocation>
	val kpIndexFormatter: SingleValueFormatter<KpIndex>
	val visibilityFormatter: SingleValueFormatter<Visibility>
	val chanceLevelFormatter: SingleValueFormatter<ChanceLevel>
}
