package se.gustavkarlsson.skylight.android.services_impl.providers

import org.jetbrains.anko.AnkoLogger
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider

class AuroraReportFromCacheProvider(
        private val cache: SingletonCache<AuroraReport>
) : AuroraReportProvider, AnkoLogger {

    override fun get() = cache.value
}
