package se.gustavkarlsson.skylight.android.lib.darkness

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.DarknessProvider
import se.gustavkarlsson.skylight.android.services.Time

val libDarknessModule = module {

    single<DarknessProvider> {
        KlausBrunnerDarknessProvider(time = get(), pollingInterval = 1.minutes)
    }
}

@Module
class LibDarknessModule {

    @Provides
    @Reusable
    internal fun darknessProvider(time: Time): DarknessProvider =
        KlausBrunnerDarknessProvider(time, pollingInterval = 1.minutes)
}
