package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceClearer {
    fun clear(tags: Collection<ServiceTag>)
}
