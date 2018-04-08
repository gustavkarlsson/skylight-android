package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.os.Build
import dagger.Module
import io.reactivex.Single
import java.util.*

class AndroidLocalizationModule(private val context: Context) : LocalizationModule {
	override val locale: Single<Locale>
		get() = Single.fromCallable {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				context.resources.configuration.locales[0]
			} else {
				context.resources.configuration.locale
			}
		}
}
