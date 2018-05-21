package se.gustavkarlsson.skylight.android.di.modules

import android.os.Build
import io.reactivex.Single
import java.util.*

class AndroidLocalizationModule(private val contextModule: ContextModule) : LocalizationModule {

	override val locale: Single<Locale>
		get() = Single.fromCallable {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				contextModule.context.resources.configuration.locales[0]
			} else {
				@Suppress("DEPRECATION")
				contextModule.context.resources.configuration.locale
			}
		}
}
