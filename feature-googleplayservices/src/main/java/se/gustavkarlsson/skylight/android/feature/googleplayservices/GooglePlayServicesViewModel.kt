package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.feature.base.Navigator
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore

internal class GooglePlayServicesViewModel(
	private val navigator: Navigator,
	private val targetId: String
) : ViewModel() {
	fun makeGooglePlayServicesAvailable(activity: Activity): Completable =
		GoogleApiAvailability.getInstance()
			.makeGooglePlayServicesAvailable(activity).toCompletable()

	fun navigateForward() = navigator.navigate(targetId)
}
