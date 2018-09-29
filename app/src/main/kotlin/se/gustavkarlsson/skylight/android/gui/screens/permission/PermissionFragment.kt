package se.gustavkarlsson.skylight.android.gui.screens.permission


import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_permission.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.BaseFragment
import timber.log.Timber

class PermissionFragment : BaseFragment(R.layout.fragment_permission) {

	private val locationPermission: String by inject("locationPermission")

	// TODO Inject
	private val rxPermissions: RxPermissions by lazy {
		RxPermissions(requireActivity()).apply { setLogging(BuildConfig.DEBUG) }
	}

	private val viewModel: PermissionViewModel by viewModel()

	override fun bindData(scope: LifecycleScopeProvider<*>) {
		grantButton.clicks()
			.autoDisposable(scope)
			.subscribe {
				ensureLocationPermission()
			}
	}

	private fun ensureLocationPermission() {
		rxPermissions
			.requestEach(locationPermission)
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY)) // Required to not dispose for dialog
			.subscribe {
				when {
					it.granted -> {
						Timber.i("Permission met")
						viewModel.signalLocationPermissionGranted()
					}
					it.shouldShowRequestPermissionRationale -> {
						Timber.i("Showing permission rationale to user for another chance")
						showLocationPermissionRequiredDialog()
					}
					else -> {
						Timber.i("Showing permission denied dialog and exiting")
						showLocationPermissionDeniedDialog()
					}
				}
			}
	}

	private fun showLocationPermissionRequiredDialog() {
		AlertDialog.Builder(requireContext())
			.setIcon(R.drawable.ic_warning_white_24dp)
			.setTitle(getString(R.string.permission_required_title))
			.setMessage(R.string.permission_required_desc)
			.setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
			.show()
	}

	private fun showLocationPermissionDeniedDialog() {
		AlertDialog.Builder(requireContext())
			.setIcon(R.drawable.ic_warning_white_24dp)
			.setTitle(getString(R.string.error_location_permission_denied_title))
			.setMessage(R.string.error_location_permission_denied_desc)
			.setPositiveButton(R.string.exit) { _, _ -> System.exit(3) }
			.setCancelable(false)
			.show()
	}
}
