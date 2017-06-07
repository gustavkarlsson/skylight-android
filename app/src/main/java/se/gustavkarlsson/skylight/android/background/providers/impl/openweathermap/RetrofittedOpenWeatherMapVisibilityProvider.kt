package se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap

import android.util.Log
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.io.IOException

internal class RetrofittedOpenWeatherMapVisibilityProvider constructor(
		private val service: OpenWeatherMapService,
		private val appId: String
) : VisibilityProvider {

    override fun getVisibility(latitude: Double, longitude: Double): Visibility {
        try {
            val response = service.get(latitude, longitude, "json", appId).execute()
            Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString())
            if (!response.isSuccessful) {
                throw UserFriendlyException(R.string.error_could_not_determine_visibility, response.errorBody().string())
            }
            val (clouds) = response.body()
            return Visibility(clouds.percentage)
        } catch (e: IOException) {
            throw UserFriendlyException(R.string.error_could_not_determine_visibility, e)
        }

    }

    companion object {
        private val TAG = RetrofittedOpenWeatherMapVisibilityProvider::class.java.simpleName
    }
}
