package se.gustavkarlsson.skylight.android.feature.googleplayservices

import dagger.Component
import se.gustavkarlsson.skylight.android.core.AppComponent

@Component(
    dependencies = [AppComponent::class]
)
internal interface GooglePlayServicesComponent {
    fun viewModel(): GooglePlayServicesViewModel

    companion object {
        fun build(): GooglePlayServicesComponent =
            DaggerGooglePlayServicesComponent.builder()
                .appComponent(AppComponent.instance)
                .build()
    }
}
