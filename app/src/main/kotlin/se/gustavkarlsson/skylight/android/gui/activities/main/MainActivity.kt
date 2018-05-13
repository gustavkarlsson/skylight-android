package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.entities.Chance
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
		bindData()
		bindDataNew()
	}

	override fun onRequirementsMet() = Unit

	private fun bindData() {
		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(supportActionBar!!::setTitle)
			.autoDisposeOnStop()

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

		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(chance.text())
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

		bindFactor(
			viewModel.darknessValue, viewModel.darknessChance, darkness,
			R.string.factor_darkness_title_full, R.string.factor_darkness_desc,
			"darkness"
		)
		bindFactor(
			viewModel.geomagLocationValue, viewModel.geomagLocationChance, geomagLocation,
			R.string.factor_geomag_location_title_full, R.string.factor_geomag_location_desc,
			"geomagLocation"
		)
		bindFactor(
			viewModel.kpIndexValue, viewModel.kpIndexChance, kpIndex,
			R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc,
			"kpIndex"
		)
		bindFactor(
			viewModel.visibilityValue, viewModel.visibilityChance, visibility,
			R.string.factor_visibility_title_full, R.string.factor_visibility_desc,
			"visibility"
		)
	}

	private fun bindDataNew() {
		// TODO What about initial refresh?
		events
			.compose(viewModel.states)
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { state ->
				swipeRefreshLayout.isRefreshing = state.isRefreshing
			}
			.autoDisposeOnStop()
	}

	private fun bindFactor(
		values: Flowable<CharSequence>,
		chances: Flowable<Chance>,
		view: AuroraFactorView,
		titleResourceId: Int,
		descriptionResourceId: Int,
		factorDebugName: String
	) {
		values
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { view.value = it }
			.autoDisposeOnStop()

		chances
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { view.chance = it }
			.autoDisposeOnStop()

		view.clicks()
			.observeOn(AndroidSchedulers.mainThread())
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
}
