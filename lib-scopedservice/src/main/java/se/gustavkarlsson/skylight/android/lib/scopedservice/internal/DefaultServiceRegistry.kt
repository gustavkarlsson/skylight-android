package se.gustavkarlsson.skylight.android.lib.scopedservice.internal

import se.gustavkarlsson.skylight.android.ScopedService
import se.gustavkarlsson.skylight.android.services.ServiceRegistry
import timber.log.Timber

internal class DefaultServiceRegistry :
    ServiceRegistry {
    private var services = mapOf<String, ServiceEntry>()

    @Synchronized
    override fun register(id: String, tag: String, service: ScopedService) {
        require(id !in services) { "Service already exists for $id" }
        Timber.i("Registering service '$id' with tag '$tag'")
        services = services + (id to ServiceEntry(tag, service))
    }

    @Synchronized
    override fun onTagsChanged(tags: Collection<String>) {
        services.forEach { (id, entry) ->
            if (entry.tag !in tags) {
                entry.service.onCleared()
                Timber.i("Cleared service '$id' with tag '${entry.tag}'")
            }
        }
        services = services.filterValues { it.tag in tags }
    }

    @Synchronized
    override fun get(id: String): ScopedService? {
        val entry = services[id]
        logServiceRequest(id, entry)
        return entry?.service
    }

    private fun logServiceRequest(id: String, entry: ServiceEntry?) {
        val serviceDescription =
            if (entry == null) "null"
            else "service with tag '${entry.tag}'"
        Timber.i("Requested service '$id' and got $serviceDescription")
    }
}

private data class ServiceEntry(val tag: String, val service: ScopedService)
