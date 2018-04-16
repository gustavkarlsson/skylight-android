package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.extensions.invoke
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance.AuroraChanceViewModel
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.*
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.gui.views.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Analytics
import timber.log.Timber


class MainActivity : AuroraRequirementsCheckingActivity() {

	private var snackbar: Snackbar? = null

	private val viewModel: MainViewModel by lazy {
		appComponent.mainViewModel(this)
	}

	private val chanceViewModel: AuroraChanceViewModel by lazy {
		appComponent.auroraChanceViewModel(this)
	}

	private val darknessViewModel: DarknessViewModel by lazy {
		appComponent.darknessViewModel(this)
	}

	private val geomagLocationViewModel: GeomagLocationViewModel by lazy {
		appComponent.geomagLocationViewModel(this)
	}

	private val kpIndexViewModel: KpIndexViewModel by lazy {
		appComponent.kpIndexViewModel(this)
	}

	private val visibilityViewModel: VisibilityViewModel by lazy {
		appComponent.visibilityViewModel(this)
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
	}

	override fun onRequirementsMet() {
		Timber.i("Refreshing...")
		viewModel.refresh()
	}

	private fun bindData() {
		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(supportActionBar!!::setTitle)
			.autoDisposeOnStop()

		viewModel.refreshFinished
			.doOnNext { Timber.i("Finished refreshing") }
			.map { false }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(swipeRefreshLayout::setRefreshing)
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

		swipeRefreshLayout.refreshes()
			.doOnNext {
				Timber.i("Refreshing...")
				Analytics.logManualRefresh()
			}
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(viewModel.refresh)
			.autoDisposeOnStop()

		chanceViewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(chance.text())
			.autoDisposeOnStop()

		chanceViewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.text())
			.autoDisposeOnStop()

		chanceViewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate visibility: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.visibility())
			.autoDisposeOnStop()

		bindFactor(
			darknessViewModel, darkness,
			R.string.factor_darkness_title_full, R.string.factor_darkness_desc, "darkness"
		)
		bindFactor(
			geomagLocationViewModel,
			geomagLocation,
			R.string.factor_geomag_location_title_full,
			R.string.factor_geomag_location_desc,
			"geomagLocation"
		)
		bindFactor(
			kpIndexViewModel, kpIndex,
			R.string.factor_kp_index_title_full, R.string.factor_kp_index_desc, "kpIndex"
		)
		bindFactor(
			visibilityViewModel, visibility,
			R.string.factor_visibility_title_full, R.string.factor_visibility_desc, "visibility"
		)
	}

	private fun bindFactor(
		viewModel: FactorViewModel<*>,
		view: AuroraFactorView,
		titleResourceId: Int,
		descriptionResourceId: Int,
		factorDebugName: String
	) {
		viewModel.value
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe { view.value = it }
			.autoDisposeOnStop()

		viewModel.chance
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
