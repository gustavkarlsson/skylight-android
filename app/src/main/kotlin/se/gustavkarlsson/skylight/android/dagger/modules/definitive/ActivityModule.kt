package se.gustavkarlsson.skylight.android.dagger.modules.definitive

import android.app.Activity

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope

@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    @ActivityScope
    fun provideActivity(): Activity {
        return activity
    }
}
