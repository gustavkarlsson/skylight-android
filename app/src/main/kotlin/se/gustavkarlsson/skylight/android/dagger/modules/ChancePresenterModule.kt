package se.gustavkarlsson.skylight.android.dagger.modules

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services_impl.presenters.ChanceTextViewPresenter
import javax.inject.Named

@Module
class ChancePresenterModule {

    @Provides
    @FragmentScope
    fun provideChancePresenter(@Named(FRAGMENT_ROOT_NAME) rootView: View): Presenter<Chance> {
        val chanceView = rootView.findViewById(R.id.chance) as TextView
        return ChanceTextViewPresenter(chanceView)
    }
}
