package se.gustavkarlsson.skylight.android.gui.screens.googleplayservices

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.GoogleApiAvailability
import io.ashdavies.rx.rxtasks.toCompletable
import io.reactivex.Completable
import se.gustavkarlsson.skylight.android.krate.Command
import se.gustavkarlsson.skylight.android.krate.SkylightStore

class GooglePlayServicesViewModel(
	private val store: SkylightStore
) : ViewModel() {
	fun makeGooglePlayServicesAvailable(activity: Activity): Completable =
		GoogleApiAvailability.getInstance()
			.makeGooglePlayServicesAvailable(activity).toCompletable()

	fun signalGooglePlayServicesInstalled() = store.issue(Command.SignalGooglePlayServicesInstalled)
}
