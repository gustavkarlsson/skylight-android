package se.gustavkarlsson.skylight.android.feature.googleplayservices

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.appComponent

@Component(
    modules = [GooglePlayServicesModule::class],
    dependencies = [AppComponent::class]
)
internal interface GooglePlayServicesComponent {
    fun viewModel(): GooglePlayServicesViewModel

    companion object {
        fun viewModel(): GooglePlayServicesViewModel =
            DaggerGooglePlayServicesComponent.builder()
                .appComponent(appComponent)
                .build()
                .viewModel()
    }
}

@Module
internal class GooglePlayServicesModule {

    @Provides
    fun aboutViewModel(): GooglePlayServicesViewModel = GooglePlayServicesViewModel()
}
