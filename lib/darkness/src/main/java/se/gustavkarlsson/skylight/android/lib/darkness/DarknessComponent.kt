package se.gustavkarlsson.skylight.android.lib.darkness

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent

@Component
abstract class DarknessComponent internal constructor(
    @Component internal val timeComponent: TimeComponent,
) {

    @get:Provides
    val darknessFormatter: Formatter<Darkness> = DarknessFormatter

    @get:Provides
    val darknessEvaluator: ChanceEvaluator<Darkness> = DarknessEvaluator

    abstract val darknessProvider: DarknessProvider

    @Provides
    internal fun darknessProvider(impl: KlausBrunnerDarknessProvider): DarknessProvider = impl

    companion object {
        val instance: DarknessComponent = DarknessComponent::class.create(
            timeComponent = TimeComponent.instance,
        )
    }
}
