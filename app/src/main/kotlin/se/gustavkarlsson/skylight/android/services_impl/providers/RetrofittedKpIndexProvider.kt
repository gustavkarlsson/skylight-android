package se.gustavkarlsson.skylight.android.services_impl.providers

import dagger.Reusable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import java.io.IOException
import javax.inject.Inject

@Reusable
class RetrofittedKpIndexProvider
@Inject
constructor(
		private val service: KpIndexService
) : KpIndexProvider, AnkoLogger {

    suspend override fun getKpIndex(): KpIndex {
        try {
            val response = service.get().execute()
			debug("Got response: ${response.code()}, message: ${response.raw()}")
            if (!response.isSuccessful) {
                throw UserFriendlyException(R.string.error_could_not_determine_kp_index, response.errorBody()!!.string())
            }
            val kpIndex = response.body()!!
            return KpIndex(kpIndex.value.toDouble())
        } catch (e: IOException) {
            throw UserFriendlyException(R.string.error_could_not_determine_kp_index, e)
        }

    }
}
