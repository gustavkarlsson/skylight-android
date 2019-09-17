package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import java.util.Locale

internal val appModule = module {

	scope("activity") { (activity: Activity) ->
		activity
	}

	scope("activity") { (fragmentManager: FragmentManager) ->
		fragmentManager
	}

	scope("activity") { (container: ViewGroup) ->
		container
	}

	single<Class<out Activity>>("activity") {
		MainActivity::class.java
	}

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

	single("versionCode") {
		BuildConfig.VERSION_CODE
	}

	single("versionName") {
		BuildConfig.VERSION_NAME
	}

}
