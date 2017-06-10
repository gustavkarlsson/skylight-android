package se.gustavkarlsson.skylight.android.background.providers.impl

import dagger.Reusable
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.background.providers.GeomagActivityProvider
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.io.IOException
import javax.inject.Inject

@Reusable
class RetrofittedGeomagActivityProvider
@Inject
constructor(
		private val service: KpIndexService
) : GeomagActivityProvider {

    override fun getGeomagActivity(): GeomagActivity {
        try {
            val response = service.get().execute()
            // TODO Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString())
            if (!response.isSuccessful) {
                throw UserFriendlyException(R.string.error_could_not_determine_geomag_activity, response.errorBody().string())
            }
            val kpIndex = response.body()
            return GeomagActivity(kpIndex.value)
        } catch (e: IOException) {
            throw UserFriendlyException(R.string.error_could_not_determine_geomag_activity, e)
        }

    }
}
