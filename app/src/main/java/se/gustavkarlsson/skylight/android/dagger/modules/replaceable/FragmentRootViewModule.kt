package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.Names.FRAGMENT_ROOT_NAME
import se.gustavkarlsson.skylight.android.dagger.scopes.FragmentScope
import javax.inject.Named

@Module
class FragmentRootViewModule(
		private val inflater: LayoutInflater,
		private val container: ViewGroup?,
		private val fragmentId: Int
) {

    // Published
    @Provides
    @FragmentScope
    @Named(FRAGMENT_ROOT_NAME)
    fun provideRootView(): View {
        return inflater.inflate(fragmentId, container, false)
    }
}
