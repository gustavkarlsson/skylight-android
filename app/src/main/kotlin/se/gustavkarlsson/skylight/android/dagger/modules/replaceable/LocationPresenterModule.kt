package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.LocationPresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import javax.inject.Named

@Module
class LocationPresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideLocationPresenter(
			@Named(FRAGMENT_ROOT_NAME) rootView: View
	): Presenter<String?> {
        val locationView = rootView.findViewById(R.id.location) as TextView
        return LocationPresenter(locationView)
    }
}
