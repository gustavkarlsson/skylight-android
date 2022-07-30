package se.gustavkarlsson.skylight.android.feature.about

import com.squareup.anvil.annotations.MergeComponent
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent

@MergeComponent(
    scope = AboutScopeMarker::class,
    dependencies = [
        AppComponent::class,
        TimeComponent::class,
    ],
)
internal interface AboutComponent {
    fun viewModel(): AboutViewModel

    companion object {
        fun build(): AboutComponent =
            DaggerAboutComponent.builder()
                .appComponent(AppComponent.instance)
                .timeComponent(TimeComponent.instance)
                .build()
    }
}

abstract class AboutScopeMarker private constructor()
