package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.background.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.background.providers.impl.KlausBrunnerDarknessProvider

@Module
abstract class DarknessModule {

    @Binds
    @Reusable
    abstract fun bindDarknessProvider(impl: KlausBrunnerDarknessProvider): DarknessProvider

}
