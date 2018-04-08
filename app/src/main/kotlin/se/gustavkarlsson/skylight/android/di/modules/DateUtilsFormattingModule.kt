package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.*
import java.util.*

class DateUtilsFormattingModule(context: Context, locale: Single<Locale>) : FormattingModule {

	override val relativeTimeFormatter: RelativeTimeFormatter by lazy {
		DateUtilsRelativeTimeFormatter({ context.getString(R.string.right_now) })
	}

	override val darknessFormatter: SingleValueFormatter<Darkness> by lazy { DarknessFormatter() }

	override val geomagLocationFormatter: SingleValueFormatter<GeomagLocation> by lazy {
		GeomagLocationFormatter(locale)
	}

	override val kpIndexFormatter: SingleValueFormatter<KpIndex> by lazy { KpIndexFormatter() }

	override val visibilityFormatter: SingleValueFormatter<Visibility> by lazy { VisibilityFormatter() }

	override val chanceLevelFormatter: SingleValueFormatter<ChanceLevel> by lazy {
		ChanceLevelFormatter(context)
	}
}
