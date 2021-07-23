package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ScopedServiceComponent {

    fun serviceClearer(): ServiceClearer

    fun serviceCatalog(): ServiceCatalog

    interface Setter {
        fun setScopedServiceComponent(component: ScopedServiceComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: ScopedServiceComponent
            private set
    }
}
