package se.gustavkarlsson.skylight.android

import android.app.Application
import android.util.Log
import com.evernote.android.job.JobManager
import com.jakewharton.threetenabp.AndroidThreeTen
import se.gustavkarlsson.skylight.android.background.UpdateJob
import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule

class Skylight : Application() {

	override fun onCreate() {
		Log.v(TAG, "onCreate")
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
				UpdateJob.UPDATE_JOB_TAG -> applicationComponent.getUpdateJob()
				else -> null
			}
		}
	}

	companion object {
		private val TAG = Skylight::class.java.simpleName

		lateinit var applicationComponent: ApplicationComponent
			private set
	}

}
