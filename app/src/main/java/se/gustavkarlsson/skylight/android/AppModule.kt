package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.fragment.app.FragmentManager
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.lib.ui.Navigator
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.PermissionChecker
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import java.util.Locale

internal val appModule = module {

	scope("activity") { (activity: Activity) ->
		activity
	}

	scope("activity") { (fragmentManager: FragmentManager) ->
		fragmentManager
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

	scope("activity") {
		Navigator(
			fragmentManager = get(),
			containerId = R.id.fragmentContainer, // FIXME injected?
			destinationRegistry = get()
		)
	}

	single("versionCode") {
		BuildConfig.VERSION_CODE
	}

	single("versionName") {
		BuildConfig.VERSION_NAME
	}

	single("main") { _ ->
		val permissionChecker = get<PermissionChecker>()
		val auroraReportProvider = get<AuroraReportProvider>()
		val placesRepo = get<PlacesRepository>()

		buildSkylightStore(permissionChecker, placesRepo, auroraReportProvider)
	}

	single("state") {
		get<SkylightStore>("main").states
	}

}
