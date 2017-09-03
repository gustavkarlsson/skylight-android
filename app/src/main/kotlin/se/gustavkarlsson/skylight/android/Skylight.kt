package se.gustavkarlsson.skylight.android

import android.app.Application
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.ContextModule
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob.Companion.UPDATE_JOB_TAG

class Skylight : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        applicationComponent = DaggerApplicationComponent.builder()
                .contextModule(ContextModule(this))
                .build()
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
