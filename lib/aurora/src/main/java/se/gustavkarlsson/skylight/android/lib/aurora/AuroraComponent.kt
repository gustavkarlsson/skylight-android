package se.gustavkarlsson.skylight.android.lib.aurora

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@ContributesTo(AppScopeMarker::class)
interface AuroraComponent {

    fun auroraReportProvider(): AuroraReportProvider

    fun auroraReportChanceEvaluator(): ChanceEvaluator<AuroraReport>

    fun chanceLevelFormatter(): Formatter<ChanceLevel>

    interface Setter {
        fun setAuroraComponent(component: AuroraComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: AuroraComponent
            private set
    }
}
