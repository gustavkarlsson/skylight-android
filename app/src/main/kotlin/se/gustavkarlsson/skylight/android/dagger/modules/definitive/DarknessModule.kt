package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.KlausBrunnerDarknessProvider

@Module
abstract class DarknessModule {

    @Binds
    @Reusable
    abstract fun bindDarknessProvider(impl: KlausBrunnerDarknessProvider): DarknessProvider

}
