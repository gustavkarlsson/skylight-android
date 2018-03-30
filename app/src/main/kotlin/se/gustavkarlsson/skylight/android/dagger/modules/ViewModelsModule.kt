package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.DEFAULT_LOCATION_NAME
import se.gustavkarlsson.skylight.android.dagger.RIGHT_NOW_THRESHOLD_NAME
import javax.inject.Named

@Module
class ViewModelsModule {

	@Provides
	@Reusable
	@Named(RIGHT_NOW_THRESHOLD_NAME)
	fun provideRightNowThreshold(): Duration = RIGHT_NOW_THRESHOLD

	@Provides
	@Reusable
	@Named(DEFAULT_LOCATION_NAME)
	fun provideDefaultLocationName(context: Context): CharSequence = context.getString(R.string.your_location)

	companion object {
	    private val RIGHT_NOW_THRESHOLD = Duration.ofMinutes(1)
	}
}
