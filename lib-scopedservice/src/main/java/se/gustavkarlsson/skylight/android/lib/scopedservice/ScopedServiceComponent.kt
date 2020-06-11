package se.gustavkarlsson.skylight.android.lib.scopedservice

interface ScopedServiceComponent {

    fun serviceRegistry(): ServiceRegistry

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
