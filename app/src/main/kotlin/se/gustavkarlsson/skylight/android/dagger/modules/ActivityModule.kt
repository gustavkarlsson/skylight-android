package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.Activity
import android.support.v7.app.AppCompatActivity

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope

@Module
class ActivityModule(
        private val activity: AppCompatActivity
) {

    @Provides
    @ActivityScope
    fun provideActivity(): Activity = activity

	@Provides
	@ActivityScope
	fun provideAppCompatActivity(): AppCompatActivity = activity
}
