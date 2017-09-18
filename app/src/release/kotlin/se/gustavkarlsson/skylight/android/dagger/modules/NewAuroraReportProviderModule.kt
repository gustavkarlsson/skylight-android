package se.gustavkarlsson.skylight.android.dagger.modules

@Module
class NewAuroraReportProviderModule {

    @Provides
    fun provideAuroraReportProvider(
            @Named(LAST_NAME) cache: SingletonCache<AuroraReport>,
            connectivityManager: ConnectivityManager,
            locationProvider: LocationProvider,
            auroraFactorsProvider: AuroraFactorsProvider,
            asyncAddressProvider: AsyncAddressProvider,
            clock: Clock
    ): AuroraReportProvider = RealAuroraReportProvider(cache, connectivityManager, locationProvider, auroraFactorsProvider, asyncAddressProvider, clock, Duration.ofSeconds(30))

}
