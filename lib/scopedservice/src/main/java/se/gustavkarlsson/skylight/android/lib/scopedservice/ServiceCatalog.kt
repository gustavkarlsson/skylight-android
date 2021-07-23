package se.gustavkarlsson.skylight.android.lib.scopedservice

// FIXME Make tag a different type
interface ServiceCatalog {
    fun register(id: String, tag: String, service: ScopedService)
    operator fun get(id: String): ScopedService?
}
