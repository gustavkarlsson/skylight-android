package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.Settings

@Module
class CustomSettingsModule(
	private val settings: Settings
) {

	@Provides
    @Reusable
    fun provideSettings(): Settings = settings
}
