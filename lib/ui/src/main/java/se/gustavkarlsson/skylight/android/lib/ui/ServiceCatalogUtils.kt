package se.gustavkarlsson.skylight.android.lib.ui

import android.app.Activity
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceHost
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceId
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceTag

fun Activity.registerService(id: ServiceId, tag: ServiceTag, service: ScopedService) {
    val host = this as? ServiceHost ?: error("Activity does not implement ${ScopedService::class.java.name}")
    host.serviceCatalog.register(id, tag, service)
}

inline fun <reified T : ScopedService> Activity.getService(id: ServiceId): T? {
    val host = this as? ServiceHost ?: error("Activity does not implement ${ScopedService::class.java.name}")
    val service = host.serviceCatalog[id] ?: return null
    check(service is T) {
        "Service is not of type: ${T::class.java.name} (${service.javaClass.name})"
    }
    return service
}

inline fun <reified T : ScopedService> Activity.getOrRegisterService(
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
