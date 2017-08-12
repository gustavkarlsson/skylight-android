package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.View
import android.widget.TextView
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.dagger.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.ChancePresenter
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import javax.inject.Named

@Module
class ChancePresenterModule {

    // Published
    @Provides
    @FragmentScope
    fun provideChancePresenter(@Named(FRAGMENT_ROOT_NAME) rootView: View): Presenter<Chance> {
        val chanceView = rootView.findViewById(R.id.chance) as TextView
        return ChancePresenter(chanceView)
    }
}
