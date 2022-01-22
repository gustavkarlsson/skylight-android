package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
object LibDarknessModule {

    @Provides
    @Reusable
    internal fun darknessformatter(): Formatter<Darkness> = DarknessFormatter

    @Provides
    @Reusable
    internal fun darknessEvaluator(): ChanceEvaluator<Darkness> = DarknessEvaluator

    @Provides
    internal fun darknessProvider(impl: KlausBrunnerDarknessProvider): DarknessProvider = impl
}
