package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.extensions.invoke
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.services.Analytics
import timber.log.Timber


class MainActivity : AuroraRequirementsCheckingActivity() {

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
	}
}
