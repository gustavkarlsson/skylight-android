package se.gustavkarlsson.skylight.android

import dagger.Component
import se.gustavkarlsson.skylight.android.lib.analytics.LibAnalyticsModule
import se.gustavkarlsson.skylight.android.lib.okhttp.LibOkHttpModule
import se.gustavkarlsson.skylight.android.lib.weather.LibWeatherModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        TimeModule::class,
        LibOkHttpModule::class,
        LibWeatherModule::class,
        LibAnalyticsModule::class
    ]
)
internal interface ActualAppComponent : AppComponent
