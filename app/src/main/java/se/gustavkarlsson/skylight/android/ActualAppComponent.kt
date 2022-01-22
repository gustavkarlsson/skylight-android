package se.gustavkarlsson.skylight.android

import com.squareup.anvil.annotations.MergeComponent
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.ModuleStarter

@AppScope
@MergeComponent(AppScopeMarker::class)
internal interface ActualAppComponent {
    fun moduleStarters(): Set<ModuleStarter>
}
