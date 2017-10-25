package se.gustavkarlsson.skylight.android.dagger.modules

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.presenters.LocationActionBarPresenter

@Module
class LocationPresenterModule {

    @Provides
    @FragmentScope
    fun provideLocationPresenter(
			activity: AppCompatActivity
	): Presenter<String?> {
        val actionBar = activity.supportActionBar!!
		val defaultName = activity.getString(R.string.your_location)
        return LocationActionBarPresenter(actionBar, defaultName)
    }
}
