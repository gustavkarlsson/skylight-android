package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.gui.views.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Analytics
import timber.log.Timber


class MainActivity : AuroraRequirementsCheckingActivity() {

	private var snackbar: Snackbar? = null

	private val viewModel: MainViewModel by lazy {
		appComponent.mainViewModel(this)
	}

	private val swipeRefreshEvents: Observable<RefreshEvent> by lazy {
		swipeRefreshLayout.refreshes()
			.doOnNext {
				Timber.i("Refreshing...")
				Analytics.logManualRefresh()
			}
			.map { RefreshEvent }
	}

	private val events: Observable<out MainUiEvent> by lazy {
		swipeRefreshEvents
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		ensureRequirementsMet()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.action_settings -> {
				startActivity<SettingsActivity>(); true
			}
			else -> super.onOptionsItemSelected(item)
		}
	}

	public override fun onStart() {
		super.onStart()
		initUi()
		bindDataOld()
		bindData()
	}

	override fun onRequirementsMet() = Unit

	private fun initUi() {
		initFactor(
			darkness,
			R.string.factor_darkness_title_full,
			R.string.factor_darkness_desc
		)
		initFactor(
			geomagLocation,
			R.string.factor_geomag_location_title_full,
			R.string.factor_geomag_location_desc
		)
		initFactor(
			kpIndex,
			R.string.factor_kp_index_title_full,
			R.string.factor_kp_index_desc
		)
		initFactor(
			visibility,
			R.string.factor_visibility_title_full,
			R.string.factor_visibility_desc
		)
	}

	private fun initFactor(
		view: AuroraFactorView,
		titleResourceId: Int,
		descriptionResourceId: Int
	) {
		view.clicks()
			.subscribe {
				toastFactorInfo(titleResourceId, descriptionResourceId)
			}
			.autoDisposeOnStop()
	}

	private fun toastFactorInfo(titleResourceId: Int, descriptionResourceId: Int) {
		alert {
			iconResource = R.drawable.info_white_24dp
			title = ctx.getString(titleResourceId)
			message = ctx.getString(descriptionResourceId)
			okButton { it.dismiss() }
		}.show()
	}

	private fun bindDataOld() {
		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { toast(it) }
			.autoDisposeOnStop()

		viewModel.connectivityMessages
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe {
				snackbar?.run {
					Timber.d("Hiding connectivity message")
					dismiss()
				}
				it.ifPresent {
					Timber.d("Showing connectivity message: %s", it)
					snackbar = indefiniteErrorSnackbar(coordinatorLayout, it).apply { show() }
				}
			}
			.autoDisposeOnStop()

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.text())
			.autoDisposeOnStop()

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate visibility: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.visibility())
			.autoDisposeOnStop()
	}

	private fun bindData() {
		events
			.subscribe(viewModel.onEvent)
			.autoDisposeOnStop()

		viewModel.states
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { state ->
				swipeRefreshLayout.isRefreshing = state.isRefreshing
				supportActionBar!!.title = state.locationName
				chance.text = state.chanceLevel
				setFactor(state.darkness, darkness)
				setFactor(state.geomagLocation, geomagLocation)
				setFactor(state.kpIndex, kpIndex)
				setFactor(state.visibility, visibility)
			}
			.autoDisposeOnStop()
	}

	private fun setFactor(
		factor: MainUiState.Factor,
		view: AuroraFactorView
	) {
		view.value = factor.value
		view.chance = factor.chance
	}
}
