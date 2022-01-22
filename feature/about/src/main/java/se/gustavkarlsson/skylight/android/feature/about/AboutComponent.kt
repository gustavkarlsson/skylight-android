package se.gustavkarlsson.skylight.android.feature.about

import dagger.Component
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.lib.time.TimeComponent

@Component(
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
