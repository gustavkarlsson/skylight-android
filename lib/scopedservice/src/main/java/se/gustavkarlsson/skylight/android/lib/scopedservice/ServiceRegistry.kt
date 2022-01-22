package se.gustavkarlsson.skylight.android.lib.scopedservice

import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import javax.inject.Inject

@AppScope
internal class ServiceRegistry @Inject constructor() : ServiceCatalog, ServiceClearer {
    private val services = mutableMapOf<ServiceId, ServiceEntry>()

    @Synchronized
    override fun register(id: ServiceId, tag: ServiceTag, service: ScopedService) {
        require(id !in services.keys) { "Service already exists for ${id.value}" }
        logInfo { "Registering service '${id.value}' with tag '${tag.value}'" }
        services[id] = ServiceEntry(tag, service)
    }

    override fun clear(tags: Collection<ServiceTag>) {
        if (tags.isEmpty()) return
        val ids = services
            .mapNotNull { (id, entry) ->
                if (entry.tag in tags) {
                    id
                } else null
            }
        logInfo { "Clearing services for tags '${tags.map { it.value }}': '${ids.map { it.value }}'" }
        for (id in ids) {
            services[id]?.service?.onCleared()
            services.remove(id)
        }
    }

    @Synchronized
    override fun get(id: ServiceId): ScopedService? {
        val entry = services[id]
        logServiceRequest(id, entry)
        return entry?.service
    }

    private fun logServiceRequest(id: ServiceId, entry: ServiceEntry?) {
        val serviceDescription =
            if (entry == null) {
                "null"
            } else "service with tag '${entry.tag.value}'"
        logDebug { "Requested service '${id.value}' and got $serviceDescription" }
    }
}

private data class ServiceEntry(val tag: ServiceTag, val service: ScopedService)
