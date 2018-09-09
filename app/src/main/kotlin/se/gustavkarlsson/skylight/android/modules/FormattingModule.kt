package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.*

val formattingModule = module {

	single<RelativeTimeFormatter> {
		DateUtilsRelativeTimeFormatter { get<Context>().getString(R.string.right_now) }
	}

	single<SingleValueFormatter<Darkness>>("darkness") {
		DarknessFormatter()
	}

	single<SingleValueFormatter<GeomagLocation>>("geomagLocation") {
		GeomagLocationFormatter(get("locale"))
	}

	single<SingleValueFormatter<KpIndex>>("kpIndex") {
		KpIndexFormatter()
	}

	single<SingleValueFormatter<Weather>>("weather") {
		WeatherFormatter(get())
	}

	single<SingleValueFormatter<ChanceLevel>>("chanceLevel") {
		ChanceLevelFormatter(get())
	}

}
