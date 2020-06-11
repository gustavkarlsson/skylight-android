package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.DarknessProvider
import se.gustavkarlsson.skylight.android.services.Formatter

@Module
class LibDarknessModule {

    @Provides
    @Reusable
    internal fun darknessformatter(): Formatter<Darkness> = DarknessFormatter

    @Provides
    @Reusable
    internal fun darknessEvaluator(): ChanceEvaluator<Darkness> = DarknessEvaluator

    @Provides
    @Reusable
    internal fun darknessProvider(time: Time): DarknessProvider =
        KlausBrunnerDarknessProvider(time, pollingInterval = 1.minutes)
}
