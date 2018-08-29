package se.gustavkarlsson.skylight.android.gui.screens.permission

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_permission.*
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity
import timber.log.Timber

class PermissionFragment : Fragment(), LifecycleObserver {

	// TODO Inject
	private val rxPermissions: RxPermissions by lazy {
		RxPermissions(requireActivity()).apply { setLogging(BuildConfig.DEBUG) }
	}

	private val viewModel: PermissionViewModel by lazy {
		appComponent.permissionViewModel(this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		appCompatActivity!!.supportActionBar!!.hide()
		lifecycle.addObserver(this)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_permission, container, false)


	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {
		grantButton.clicks()
			.autoDisposable(scope(Lifecycle.Event.ON_STOP))
			.subscribe {
				ensureLocationPermission()
			}
	}

	private fun ensureLocationPermission() {
		rxPermissions
			.requestEach(appComponent.locationPermission)
			.autoDisposable(scope(Lifecycle.Event.ON_DESTROY))
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

	override fun onDestroy() {
		super.onDestroy()
		lifecycle.removeObserver(this)
	}
}
