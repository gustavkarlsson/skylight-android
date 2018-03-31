package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.qualifiers.DefaultLocationName
import se.gustavkarlsson.skylight.android.dagger.qualifiers.RightNowThresholdName

@Module
class ViewModelsModule {

	@Provides
	@Reusable
	@RightNowThresholdName
	fun provideRightNowThreshold(): Duration = RIGHT_NOW_THRESHOLD

	@Provides
	@Reusable
	@DefaultLocationName
	fun provideDefaultLocationName(context: Context): CharSequence = context.getString(R.string.your_location)

	companion object {
	    private val RIGHT_NOW_THRESHOLD = Duration.ofMinutes(1)
	}
}
