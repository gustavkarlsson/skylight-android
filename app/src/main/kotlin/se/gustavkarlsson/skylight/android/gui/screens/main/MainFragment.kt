package se.gustavkarlsson.skylight.android.gui.screens.main

import android.Manifest
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.findNavController
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.services.Analytics
import timber.log.Timber

class MainFragment : Fragment(), LifecycleObserver {

	private var snackbar: Snackbar? = null

	private var dialog: DialogInterface? = null

	private val viewModel: MainViewModel by lazy {
		appComponent.mainViewModel(this)
	}

	private val rxPermissions: RxPermissions by lazy {
		RxPermissions(requireActivity()).apply { setLogging(BuildConfig.DEBUG) }
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
		lifecycle.addObserver(this)
	}

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		inflater.inflate(R.menu.menu_main, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> {
				findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
				true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_main, container, false)

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {
		val scope = scope(Lifecycle.Event.ON_STOP)
		swipeRefreshLayout.refreshes()
			.doOnNext {
				Timber.i("Triggering refresh")
				Analytics.logManualRefresh()
			}
			.autoDisposable(scope)
			.subscribe(viewModel.swipedToRefresh)

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope)
			.subscribe(locationName.text())

		viewModel.isRefreshing
			.doOnNext { Timber.i("Refreshing: $it") }
			.autoDisposable(scope)
			.subscribe(swipeRefreshLayout::setRefreshing)

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.autoDisposable(scope)
			.subscribe { activity?.toast(it) }

		viewModel.connectivityMessages
			.autoDisposable(scope)
			.subscribe {
				snackbar?.run {
					Timber.d("Hiding connectivity message")
					dismiss()
				}
				it.ifPresent {
					Timber.d("Showing connectivity message: %s", it)
					view?.let { view ->
						snackbar = indefiniteErrorSnackbar(view, it).apply { show() }
					}
				}
			}

		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.autoDisposable(scope)
			.subscribe(chance.text())

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate.text())

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate weather: %s", it) }
			.autoDisposable(scope)
			.subscribe(timeSinceUpdate.visibility())

		viewModel.showDialog
			.doOnNext { Timber.d("Showing dialog: %s", it) }
			.autoDisposable(scope)
			.subscribe {
				dialog?.dismiss()
				dialog = activity?.alert {
					iconResource = R.drawable.info_white_24dp
					titleResource = it.titleResource
					messageResource = it.messageResource
					okButton { viewModel.hideDialogClicked.accept(Unit) }
					onCancelled { viewModel.hideDialogClicked.accept(Unit) }
				}?.show()
			}

		viewModel.hideDialog
			.doOnNext { Timber.d("Hiding dialog") }
			.autoDisposable(scope)
			.subscribe { dialog?.dismiss() }

		viewModel.ensureLocationPermission
			.doOnNext { Timber.d("Location permission is unknown") }
			.autoDisposable(scope)
			.subscribe {
				ensureLocationPermission()
			}

		FactorPresenter(
			viewModel.darknessValue, viewModel.darknessChance,
			darknessValue, darknessBar, darknessCard,
			viewModel.darknessFactorClicked,
			scope,
			"darkness"
		).present()
		FactorPresenter(
			viewModel.geomagLocationValue, viewModel.geomagLocationChance,
			geomagLocationValue, geomagLocationBar, geomagLocationCard,
			viewModel.geomagLocationFactorClicked,
			scope,
			"geomagLocation"
		).present()
		FactorPresenter(
			viewModel.kpIndexValue, viewModel.kpIndexChance,
			kpIndexValue, kpIndexBar, kpIndexCard,
			viewModel.kpIndexFactorClicked,
			scope,
			"kpIndex"
		).present()
		FactorPresenter(
			viewModel.weatherValue, viewModel.weatherChance,
			weatherValue, weatherBar, weatherCard,
			viewModel.weatherFactorClicked,
			scope,
			"weather"
		).present()
	}

	private fun ensureLocationPermission() {
		rxPermissions
			.requestEach(LOCATION_PERMISSION)
			.autoDisposable(scope(Lifecycle.Event.ON_STOP))
			.subscribe {
				when {
					it.granted -> {
						Timber.i("Permission met")
						viewModel.reportLocationPermissionGranted.accept(Unit)
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
			.setIcon(R.drawable.warning_white_24dp)
			.setTitle(getString(R.string.location_permission_required_title))
			.setMessage(R.string.location_permission_required_desc)
			.setPositiveButton(android.R.string.yes) { _, _ -> ensureLocationPermission() }
			.setNegativeButton(R.string.exit) { _, _ -> System.exit(2) }
			.setCancelable(false)
			.show()
	}

	private fun showLocationPermissionDeniedDialog() {
		AlertDialog.Builder(requireContext())
			.setIcon(R.drawable.warning_white_24dp)
			.setTitle(getString(R.string.error_location_permission_denied_title))
			.setMessage(R.string.error_location_permission_denied_desc)
			.setPositiveButton(R.string.exit) { _, _ -> System.exit(3) }
			.setCancelable(false)
			.show()
	}

	override fun onDestroy() {
		super.onDestroy()
		dialog?.dismiss()
		snackbar?.dismiss()
		lifecycle.removeObserver(this)
	}

	companion object {
		private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
	}
}
