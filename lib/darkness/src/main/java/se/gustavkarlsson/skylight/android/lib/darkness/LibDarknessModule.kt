package se.gustavkarlsson.skylight.android.lib.darkness

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@Module
@ContributesTo(AppScopeMarker::class)
object LibDarknessModule {

    @Provides
    internal fun darknessformatter(): Formatter<Darkness> = DarknessFormatter

    @Provides
    internal fun darknessEvaluator(): ChanceEvaluator<Darkness> = DarknessEvaluator

    @Provides
    internal fun darknessProvider(impl: KlausBrunnerDarknessProvider): DarknessProvider = impl
}
