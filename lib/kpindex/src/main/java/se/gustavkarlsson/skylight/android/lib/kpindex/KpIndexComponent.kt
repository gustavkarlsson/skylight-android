package se.gustavkarlsson.skylight.android.lib.kpindex

import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

interface KpIndexComponent {

    fun kpIndexChanceEvaluator(): ChanceEvaluator<KpIndex>

    fun kpIndexFormatter(): Formatter<KpIndex>

    interface Setter {
        fun setKpIndexComponent(component: KpIndexComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: KpIndexComponent
            private set
    }
}
