package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.ChanceLevelFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.DarknessFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.DateUtilsRelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.GeomagLocationFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.KpIndexFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.WeatherFormatter

val formattingModule = module {

	single<RelativeTimeFormatter> {
		val rightNowText = get<Context>().getString(R.string.right_now)
		DateUtilsRelativeTimeFormatter(rightNowText)
	}

	single<SingleValueFormatter<Darkness>>("darkness") {
		DarknessFormatter
	}

	single<SingleValueFormatter<GeomagLocation>>("geomagLocation") {
		GeomagLocationFormatter(get("locale"))
	}

	single<SingleValueFormatter<KpIndex>>("kpIndex") {
		KpIndexFormatter
	}

	single<SingleValueFormatter<Weather>>("weather") {
		WeatherFormatter
	}

	single<SingleValueFormatter<ChanceLevel>>("chanceLevel") {
		ChanceLevelFormatter
	}

}
