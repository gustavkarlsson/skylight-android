package se.gustavkarlsson.skylight.android

import android.app.Application
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob.Companion.UPDATE_JOB_TAG

class Skylight : Application() {

	lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
		instance = this
        component = DaggerApplicationComponent.builder()
                .contextModule(ContextModule(this))
                .build()
		AndroidThreeTen.init(this)
        val setupNotifications = component.getSetupNotifications()
		setupNotifications()
        initJobManager()
    }

    private fun initJobManager() {
        val jobManager = JobManager.create(this)
        jobManager.addJobCreator { tag ->
            when (tag) {
                UPDATE_JOB_TAG -> component.getUpdateJob()
                else -> null
            }
        }
    }

	companion object {
		lateinit var instance: Skylight
	}

}
