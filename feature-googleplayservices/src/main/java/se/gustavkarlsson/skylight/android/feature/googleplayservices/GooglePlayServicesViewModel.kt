package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.lib.ui.Navigator

internal class GooglePlayServicesViewModel(
	private val navigator: Navigator
) : ViewModel() {
	fun makeGooglePlayServicesAvailable(activity: Activity): Completable =
		GoogleApiAvailability.getInstance()
			.makeGooglePlayServicesAvailable(activity).toCompletable()

	fun navigateToMain() = navigator.navigate("main", false)
}
