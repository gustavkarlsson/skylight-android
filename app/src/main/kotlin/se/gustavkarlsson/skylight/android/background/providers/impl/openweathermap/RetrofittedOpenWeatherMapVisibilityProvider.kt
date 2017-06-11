package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.models.Visibility
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.io.IOException

class RetrofittedOpenWeatherMapVisibilityProvider constructor(
		private val service: OpenWeatherMapService,
		private val appId: String
) : VisibilityProvider, AnkoLogger {

    override fun getVisibility(latitude: Double, longitude: Double): Visibility {
        try {
            val response = service.get(latitude, longitude, "json", appId).execute()
            debug("Got response: ${response.code()}, message: ${response.raw()}")
            if (!response.isSuccessful) {
                throw UserFriendlyException(R.string.error_could_not_determine_visibility, response.errorBody().string())
            }
            val (clouds) = response.body()
            return Visibility(clouds.percentage)
        } catch (e: IOException) {
            throw UserFriendlyException(R.string.error_could_not_determine_visibility, e)
        }

    }
}
