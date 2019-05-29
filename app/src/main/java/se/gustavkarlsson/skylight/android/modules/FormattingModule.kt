package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.gui.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.Formatter
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

	single<Formatter<Darkness>>("darkness") {
		DarknessFormatter
	}

	single<Formatter<GeomagLocation>>("geomagLocation") {
		GeomagLocationFormatter(get("locale"))
	}

	single<Formatter<KpIndex>>("kpIndex") {
		KpIndexFormatter
	}

	single<Formatter<Weather>>("weather") {
		WeatherFormatter
	}

	single<Formatter<ChanceLevel>>("chanceLevel") {
		ChanceLevelFormatter
	}

}
