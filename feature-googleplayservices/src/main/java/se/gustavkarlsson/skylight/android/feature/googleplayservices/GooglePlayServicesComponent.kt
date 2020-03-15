package se.gustavkarlsson.skylight.android.feature.googleplayservices

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
@Component(
    modules = [GooglePlayServicesModule::class],
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

@Module
internal class GooglePlayServicesModule {

    @Provides
    fun viewModel(): GooglePlayServicesViewModel = GooglePlayServicesViewModel()
}
