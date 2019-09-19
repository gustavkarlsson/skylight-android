package se.gustavkarlsson.skylight.android.lib.geocoder

import io.reactivex.Single
import org.koin.dsl.module.module
import java.util.Locale

val libGeocoderModule = module {

	single<Geocoder> {
		val locale = get<Single<Locale>>("locale")
		MapboxGeocoder(
			accessToken = BuildConfig.MAPBOX_API_KEY,
			getLocale = locale::blockingGet
		)
	}
}
