package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceCatalog {
    fun register(id: ServiceId, tag: ServiceTag, service: ScopedService)
    operator fun get(id: ServiceId): ScopedService?
}
