package se.gustavkarlsson.skylight.android.di.modules

import android.app.Application
import android.content.Context

class ApplicationContextModule(
	application: Application
) : ContextModule {
	override val context: Context = application
}
