package se.gustavkarlsson.skylight.android.lib.darkness

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@ContributesTo(AppScopeMarker::class)
interface DarknessComponent {

    fun darknessChanceEvaluator(): ChanceEvaluator<Darkness>

    fun darknessFormatter(): Formatter<Darkness>

    interface Setter {
        fun setDarknessComponent(component: DarknessComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: DarknessComponent
            private set
    }
}
