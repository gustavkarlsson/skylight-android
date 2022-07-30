package se.gustavkarlsson.skylight.android

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    interface Setter {
        fun setMainActivityComponent(component: MainActivityComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: MainActivityComponent
            private set
    }
}
