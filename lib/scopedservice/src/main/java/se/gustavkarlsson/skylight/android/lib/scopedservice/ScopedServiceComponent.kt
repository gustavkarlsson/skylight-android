package se.gustavkarlsson.skylight.android.lib.scopedservice

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface ScopedServiceComponent {

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
