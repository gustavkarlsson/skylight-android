package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceRegistry {
    fun register(id: String, tag: String, service: ScopedService)
    operator fun get(id: String): ScopedService?
    fun onTagsChanged(tags: Collection<String>)
    fun clear() = onTagsChanged(emptySet())
}
