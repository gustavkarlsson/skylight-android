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
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.tbruyelle.rxpermissions2.RxPermissions
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.BuildConfig
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity
import se.gustavkarlsson.skylight.android.extensions.findNavController
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.views.AuroraFactorView
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
		val disposeEvent = Lifecycle.Event.ON_STOP

		swipeRefreshLayout.refreshes()
			.doOnNext {
				Timber.i("Triggering refresh")
				Analytics.logManualRefresh()
			}
			.autoDisposable(scope(disposeEvent))
			.subscribe(viewModel.swipedToRefresh)

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope(disposeEvent))
			.subscribe {
				appCompatActivity?.supportActionBar?.title = it
			}

		viewModel.isRefreshing
			.doOnNext { Timber.i("Refreshing: $it") }
			.autoDisposable(scope(disposeEvent))
			.subscribe(swipeRefreshLayout::setRefreshing)

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.autoDisposable(scope(disposeEvent))
			.subscribe { activity?.toast(it) }

		viewModel.connectivityMessages
			.autoDisposable(scope(disposeEvent))
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
			.autoDisposable(scope(disposeEvent))
			.subscribe(chance.text())

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.autoDisposable(scope(disposeEvent))
			.subscribe(timeSinceUpdate.text())

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate weather: %s", it) }
			.autoDisposable(scope(disposeEvent))
			.subscribe(timeSinceUpdate.visibility())

		viewModel.showDialog
			.doOnNext { Timber.d("Showing dialog: %s", it) }
			.autoDisposable(scope(disposeEvent))
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
			.autoDisposable(scope(disposeEvent))
			.subscribe { dialog?.dismiss() }

		viewModel.ensureLocationPermission
			.doOnNext { Timber.d("Location permission is unknown") }
			.autoDisposable(scope(disposeEvent))
			.subscribe {
				ensureLocationPermission()
			}

		bindFactor(
			viewModel.darknessValue, viewModel.darknessChance, darkness,
			viewModel.darknessFactorClicked,
			disposeEvent,
			"darkness"
		)
		bindFactor(
			viewModel.geomagLocationValue, viewModel.geomagLocationChance, geomagLocation,
			viewModel.geomagLocationFactorClicked,
			disposeEvent,
			"geomagLocation"
		)
		bindFactor(
			viewModel.kpIndexValue, viewModel.kpIndexChance, kpIndex,
			viewModel.kpIndexFactorClicked,
			disposeEvent,
			"kpIndex"
		)
		bindFactor(
			viewModel.weatherValue, viewModel.weatherChance, weather,
			viewModel.weatherFactorClicked,
			disposeEvent,
			"weather"
		)
	}

	private fun bindFactor(
		values: Observable<CharSequence>,
		chances: Observable<Chance>,
		view: AuroraFactorView,
		clickConsumer: Consumer<Unit>,
		disposeEvent: Lifecycle.Event,
		factorDebugName: String
	) {
		values
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.autoDisposable(scope(disposeEvent))
			.subscribe { view.value = it }

		chances
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.autoDisposable(scope(disposeEvent))
			.subscribe { view.chance = it }

		view.clicks()
			.autoDisposable(scope(disposeEvent))
			.subscribe(clickConsumer)
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
