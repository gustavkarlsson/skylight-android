package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.ScopedService

interface ServiceRegistry {
    fun register(id: String, tag: String, service: ScopedService)
    operator fun get(id: String): ScopedService?
    fun onTagsChanged(tags: Collection<String>)
    fun clear() = onTagsChanged(emptySet())
}
