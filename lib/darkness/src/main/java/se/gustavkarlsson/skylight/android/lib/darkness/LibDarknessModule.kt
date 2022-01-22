package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.core.utils.minutes
import se.gustavkarlsson.skylight.android.lib.time.Time

@Module
object LibDarknessModule {

    @Provides
    @Reusable
    internal fun darknessformatter(): Formatter<Darkness> = DarknessFormatter

    @Provides
    @Reusable
    internal fun darknessEvaluator(): ChanceEvaluator<Darkness> = DarknessEvaluator

    // FIXME clean up
    @Provides
    @Reusable
    internal fun darknessProvider(time: Time): DarknessProvider =
        KlausBrunnerDarknessProvider(time, pollingInterval = 1.minutes)
}
