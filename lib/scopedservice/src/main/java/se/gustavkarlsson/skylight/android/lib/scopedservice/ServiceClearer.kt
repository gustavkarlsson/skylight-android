package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceClearer {
    fun clear(tag: ServiceTag) // FIXME Collection?
}
