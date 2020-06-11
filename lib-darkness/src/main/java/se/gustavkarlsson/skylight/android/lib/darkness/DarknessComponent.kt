package se.gustavkarlsson.skylight.android.lib.darkness

import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter

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
