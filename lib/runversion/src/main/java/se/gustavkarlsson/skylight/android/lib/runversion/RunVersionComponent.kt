package se.gustavkarlsson.skylight.android.lib.runversion

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.CoreComponent

@Component
abstract class RunVersionComponent internal constructor(
    @Component internal val coreComponent: CoreComponent,
) {

    abstract val runVersionManager: RunVersionManager

    @Provides
    internal fun runVersionManager(impl: DataStoreRunVersionManager): RunVersionManager = impl

    companion object {
        val instance: RunVersionComponent = RunVersionComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}
