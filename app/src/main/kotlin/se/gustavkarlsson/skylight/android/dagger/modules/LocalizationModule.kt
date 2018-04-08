package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.os.Build
import dagger.Module
import dagger.Provides
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.BuildConfig
import java.util.*
import javax.inject.Singleton

@Module
class LocalizationModule {

    @Provides
    @Singleton
    fun provideLocale(context: Context): Single<Locale> =
		Single.fromCallable {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				context.resources.configuration.locales[0]
			} else {
				context.resources.configuration.locale
			}
		}

}
