package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.TimeSinceUpdatePresenter
import javax.inject.Named

@Module
class TimeSinceUpdatePresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideTimeSinceUpdatePresenter(
			@Named(FRAGMENT_ROOT_NAME) rootView: View,
			clock: Clock
	): TimeSinceUpdatePresenter {
        val timeSinceUpdateView = rootView.findViewById(R.id.time_since_update) as TextView
        return TimeSinceUpdatePresenter(timeSinceUpdateView, Duration.ofMinutes(1), clock)
    }
}
