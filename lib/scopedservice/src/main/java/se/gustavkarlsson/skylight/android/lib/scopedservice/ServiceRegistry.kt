package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ServiceRegistry : ServiceCatalog {
    fun onTagsChanged(tags: Collection<String>)
}
