package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.formatters.RelativeTimeFormatter
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.formatters.*
import java.util.*

@Module
class FormattingModule {

	@Provides
	@Reusable
	fun provideRelativeTimeFormatter(context: Context): RelativeTimeFormatter =
		DateUtilsRelativeTimeFormatter(
			context.getString(R.string.right_now)
		)

	@Provides
	@Reusable
	fun provideDarknessFormatter(): SingleValueFormatter<Darkness> = DarknessFormatter()

	@Provides
	@Reusable
	fun provideGeomagLocationFormatter(locale: Locale): SingleValueFormatter<GeomagLocation> =
		GeomagLocationFormatter(locale)

	@Provides
	@Reusable
	fun provideKpIndexFormatter(): SingleValueFormatter<KpIndex> = KpIndexFormatter()

	@Provides
	@Reusable
	fun provideVisibilityFormatter(): SingleValueFormatter<Visibility> = VisibilityFormatter()

}
