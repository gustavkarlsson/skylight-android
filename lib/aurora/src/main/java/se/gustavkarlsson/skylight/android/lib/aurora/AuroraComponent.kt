package se.gustavkarlsson.skylight.android.lib.aurora

import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

interface AuroraComponent {

    fun auroraReportProvider(): AuroraReportProvider

    fun completeAuroraReportChanceEvaluator(): ChanceEvaluator<CompleteAuroraReport>

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
