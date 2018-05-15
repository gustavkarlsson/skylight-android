package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.extensions.doWhen
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.gui.views.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Analytics
import timber.log.Timber


class MainActivity : AuroraRequirementsCheckingActivity(), LifecycleObserver {

	init {
	    lifecycle.addObserver(this)
	}

	private var snackbar: Snackbar? = null

	private val viewModel: MainViewModel by lazy {
		appComponent.mainViewModel(this)
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

	override fun onRequirementsMet() = Unit

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {
		swipeRefreshLayout.refreshes()
			.doOnNext {
				Timber.i("Triggering refresh")
				Analytics.logManualRefresh()
			}
			.autoDisposable(scope())
			.subscribe(viewModel.refresh)

		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.autoDisposable(scope())
			.subscribe(supportActionBar!!::setTitle)

		viewModel.isRefreshing
			.doOnNext { Timber.i("Refreshing: $it") }
			.autoDisposable(scope())
			.subscribe(swipeRefreshLayout::setRefreshing)

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.autoDisposable(scope())
			.subscribe { toast(it) }

		viewModel.connectivityMessages
			.autoDisposable(scope())
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

		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.autoDisposable(scope())
			.subscribe(chance.text())

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.autoDisposable(scope())
			.subscribe(timeSinceUpdate.text())

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate visibility: %s", it) }
			.autoDisposable(scope())
			.subscribe(timeSinceUpdate.visibility())

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

	private fun bindFactor(
		values: Observable<CharSequence>,
		chances: Observable<Chance>,
		view: AuroraFactorView,
		titleResourceId: Int,
		descriptionResourceId: Int,
		factorDebugName: String
	) {
		values
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.autoDisposable(scope())
			.subscribe { view.value = it }

		chances
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.autoDisposable(scope())
			.subscribe { view.chance = it }

		view.clicks()
			.autoDisposable(scope())
			.subscribe { toastFactorInfo(titleResourceId, descriptionResourceId) }
	}

	// TODO Make toasts part of state
	private fun toastFactorInfo(titleResourceId: Int, descriptionResourceId: Int) {
		alert {
			iconResource = R.drawable.info_white_24dp
			title = ctx.getString(titleResourceId)
			message = ctx.getString(descriptionResourceId)
			okButton { it.dismiss() }
		}.show().doWhen(scope(Lifecycle.Event.ON_DESTROY)) {
			it.dismiss()
		}
	}
}
