package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.providers.GeomagActivityProvider
import se.gustavkarlsson.skylight.android.entities.GeomagActivity
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.io.IOException
import javax.inject.Inject

@Reusable
class RetrofittedGeomagActivityProvider
@Inject
constructor(
		private val service: KpIndexService
) : GeomagActivityProvider, AnkoLogger {

    override fun getGeomagActivity(): GeomagActivity {
        try {
            val response = service.get().execute()
			debug("Got response: ${response.code()}, message: ${response.raw()}")
            if (!response.isSuccessful) {
                throw UserFriendlyException(R.string.error_could_not_determine_geomag_activity, response.errorBody()!!.string())
            }
            val kpIndex = response.body()!!
            return GeomagActivity(kpIndex.value)
        } catch (e: IOException) {
            throw UserFriendlyException(R.string.error_could_not_determine_geomag_activity, e)
        }

    }
}
