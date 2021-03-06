package se.gustavkarlsson.skylight.android.lib.ui

import android.app.Activity
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedService
import se.gustavkarlsson.skylight.android.lib.scopedservice.ServiceHost

fun Activity.registerService(screen: Screen, id: String, service: ScopedService) {
    val host = this as? ServiceHost ?: error("Activity does not implement ${ScopedService::class.java.name}")
    host.serviceCatalog.register(id, screen.tag, service)
}

inline fun <reified T : ScopedService> Activity.getService(id: String): T? {
    val host = this as? ServiceHost ?: error("Activity does not implement ${ScopedService::class.java.name}")
    val service = host.serviceCatalog[id] ?: return null
    check(service is T) {
        "Service is not of type: ${T::class.java.name} (${service.javaClass.name})"
    }
    return service
}

inline fun <reified T : ScopedService> Activity.getOrRegisterService(
    screen: Screen,
    id: String,
    createService: () -> T
): T {
    val existingService = getService<T>(id)
    if (existingService != null) return existingService
    val createdService = createService()
    registerService(screen, id, createdService)
    return createdService
}
