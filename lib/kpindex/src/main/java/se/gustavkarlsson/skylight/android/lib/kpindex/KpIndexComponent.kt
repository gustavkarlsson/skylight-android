package se.gustavkarlsson.skylight.android.lib.kpindex

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@ContributesTo(AppScopeMarker::class)
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
