package se.gustavkarlsson.skylight.android.lib.scopedservice

import androidx.fragment.app.Fragment

fun Fragment.registerService(id: String, service: ScopedService) {
    val tag = requireNotNull(tag) {
        "Fragment does not have a tag: ${javaClass.simpleName}"
    }
    val host = requireActivity() as? ServiceHost
        ?: error("Activity does not implement ${ScopedService::class.java.name}")
    host.serviceRegistry.register(id, tag, service)
}

inline fun <reified T : ScopedService> Fragment.getService(id: String): T? {
    val host = requireActivity() as? ServiceHost
        ?: error("Activity does not implement ${ScopedService::class.java.name}")
    val service = host.serviceRegistry[id] ?: return null
    check(service is T) {
        "Service is not of type: ${T::class.java.name} (${service.javaClass.name})"
    }
    return service
}

inline fun <reified T : ScopedService> Fragment.getOrRegisterService(
    id: String,
    createService: () -> T
): T = getService(id) ?: createService().also { registerService(id, it) }
