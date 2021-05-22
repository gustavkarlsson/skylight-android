package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceCatalog {
    fun register(id: String, tag: String, service: ScopedService)
    operator fun get(id: String): ScopedService?
}
