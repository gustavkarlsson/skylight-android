package se.gustavkarlsson.skylight.android.lib.scopedservice

import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logInfo

internal class ServiceRegistry : ServiceCatalog, ServiceClearer {
    private val services = mutableMapOf<String, ServiceEntry>()

    @Synchronized
    override fun register(id: String, tag: String, service: ScopedService) {
        require(id !in services.keys) { "Service already exists for $id" }
        logInfo { "Registering service '$id' with tag '$tag'" }
        services[id] = ServiceEntry(tag, service)
    }

    override fun clear(tag: String) {
        val ids = services
            .mapNotNull { (id, entry) ->
                if (entry.tag == tag) {
                    id
                } else null
            }
        logInfo { "Clearing services by ID:s '$ids'" }
        for (id in ids) {
            services[id]?.service?.onCleared()
            services.remove(id)
        }
    }

    @Synchronized
    override fun get(id: String): ScopedService? {
        val entry = services[id]
        logServiceRequest(id, entry)
        return entry?.service
    }

    private fun logServiceRequest(id: String, entry: ServiceEntry?) {
        val serviceDescription =
            if (entry == null) {
                "null"
            } else "service with tag '${entry.tag}'"
        logDebug { "Requested service '$id' and got $serviceDescription" }
    }
}

private data class ServiceEntry(val tag: String, val service: ScopedService)
