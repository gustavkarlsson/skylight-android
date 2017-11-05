package se.gustavkarlsson.skylight.android.dagger.modules

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.dagger.scopes.ActivityScope
import se.gustavkarlsson.skylight.android.gui.viewmodels.AuroraReportViewModel
import se.gustavkarlsson.skylight.android.gui.viewmodels.AuroraReportViewModelFactory

@Module
class AuroraReportViewModelModule {

    @Provides
    @ActivityScope
    fun provideAuroraReportViewModel(activity: AppCompatActivity, factory: AuroraReportViewModelFactory): AuroraReportViewModel {
		return ViewModelProviders.of(activity, factory).get(AuroraReportViewModel::class.java)
	}
}
