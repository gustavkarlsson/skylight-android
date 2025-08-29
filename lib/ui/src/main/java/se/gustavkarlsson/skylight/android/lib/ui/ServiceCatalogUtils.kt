package se.gustavkarlsson.skylight.android.lib.ui

import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag

fun registerService(id: ServiceId, tag: ServiceTag, service: ScopedService) {
    val serviceCatalog = ScopedServiceComponent.instance.serviceCatalog
    serviceCatalog.register(id, tag, service)
}

inline fun <reified T : ScopedService> getService(id: ServiceId): T? {
    val serviceCatalog = ScopedServiceComponent.instance.serviceCatalog
    val service = serviceCatalog[id] ?: return null
    check(service is T) {
        "Service is not of type: ${T::class.java.name} (${service.javaClass.name})"
    }
    return service
}

inline fun <reified T : ScopedService> getOrRegisterService(
    id: ServiceId,
    tag: ServiceTag,
    createService: () -> T,
): T {
    val existingService = getService<T>(id)
    if (existingService != null) return existingService
    val createdService = createService()
    registerService(id, tag, createdService)
    return createdService
}
