package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import android.os.Build
import io.reactivex.Single
import org.koin.dsl.module.module
import java.util.Locale

val localizationModule = module {

	single<Single<Locale>>("locale") {
		val context = get<Context>()
		Single.fromCallable {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				context.resources.configuration.locales[0]
			} else {
				@Suppress("DEPRECATION")
				context.resources.configuration.locale
			}
		}
	}

}
