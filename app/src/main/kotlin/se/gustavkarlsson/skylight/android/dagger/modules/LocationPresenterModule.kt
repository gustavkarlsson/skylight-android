package se.gustavkarlsson.skylight.android.dagger.modules

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.LocationTextViewPresenter
import javax.inject.Named

@Module
class LocationPresenterModule {

    @Provides
    @FragmentScope
    fun provideLocationPresenter(
			@Named(FRAGMENT_ROOT_NAME) rootView: View
	): Presenter<String?> {
        val locationView = rootView.findViewById<TextView>(R.id.location)
        return LocationTextViewPresenter(locationView)
    }
}
