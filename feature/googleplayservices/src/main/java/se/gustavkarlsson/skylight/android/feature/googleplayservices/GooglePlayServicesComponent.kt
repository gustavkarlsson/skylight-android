package se.gustavkarlsson.skylight.android.feature.googleplayservices

import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import se.gustavkarlsson.skylight.android.core.AppComponent

@MergeComponent(
    scope = GooglePlayServicesScopeMarker::class,
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

abstract class GooglePlayServicesScopeMarker private constructor()
