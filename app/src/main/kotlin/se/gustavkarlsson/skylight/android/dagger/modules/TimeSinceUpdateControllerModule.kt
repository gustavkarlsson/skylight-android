package se.gustavkarlsson.skylight.android.dagger.modules

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdateController
import javax.inject.Named

@Module
class TimeSinceUpdateControllerModule {

    @Provides
    @FragmentScope
    fun provideTimeSinceUpdateController(
			@Named(FRAGMENT_ROOT_NAME) rootView: View,
			clock: Clock
	): TimeSinceUpdateController {
        val timeSinceUpdateView = rootView.findViewById(R.id.timeSinceUpdate) as TextView
        return TimeSinceUpdateController(Duration.ofMinutes(1), timeSinceUpdateView, clock)
    }
}
