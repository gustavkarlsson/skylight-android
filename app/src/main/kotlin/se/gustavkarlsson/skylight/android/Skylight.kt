package se.gustavkarlsson.skylight.android

import android.app.Application
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob.Companion.UPDATE_JOB_TAG
import javax.inject.Inject

class Skylight : Application() {

	@Inject
	lateinit var setupNotifications: SetupNotifications

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        applicationComponent = DaggerApplicationComponent.builder()
                .contextModule(ContextModule(this))
                .build()
        applicationComponent.inject(this)
		setupNotifications()
        initJobManager()
    }

    private fun initJobManager() {
        val jobManager = JobManager.create(this)
        jobManager.addJobCreator { tag ->
            when (tag) {
                UPDATE_JOB_TAG -> applicationComponent.getUpdateJob()
                else -> null
            }
        }
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }

}
