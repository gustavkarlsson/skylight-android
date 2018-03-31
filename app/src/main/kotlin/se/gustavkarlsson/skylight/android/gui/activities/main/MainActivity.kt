package se.gustavkarlsson.skylight.android.gui.activities.main

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.jakewharton.rxbinding2.support.v4.widget.refreshes
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.extensions.forUi
import se.gustavkarlsson.skylight.android.extensions.indefiniteErrorSnackbar
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import timber.log.Timber


class MainActivity : AuroraRequirementsCheckingActivity() {

	private var snackbar: Snackbar? = null

	private val viewModel: MainViewModel by lazy {
		val factory = Skylight.instance.component.getMainViewModelFactory()
		ViewModelProviders.of(this, factory).get(MainViewModel::class.java)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
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
		ensureRequirementsMet()
		bindData()
	}
	override fun onRequirementsMet() {
		Timber.i("Refreshing...")
		viewModel.refresh.accept(Unit)
	}

	private fun bindData() {
		viewModel.locationName
			.doOnNext { Timber.d("Updating locationName view: %s", it) }
			.forUi(this)
			.subscribe(supportActionBar!!::setTitle)

		viewModel.refreshFinished
			.doOnNext { Timber.i("Finished refreshing") }
			.map { false }
			.forUi(this)
			.subscribe(swipeRefreshLayout::setRefreshing)

		viewModel.errorMessages
			.doOnNext { Timber.d("Showing error message") }
			.forUi(this)
			.subscribe { toast(it) }

		viewModel.connectivityMessages
			.doOnNext {
				if (it.isPresent) {
					Timber.d("Showing connectivity message: %s", it.get())
				} else {
					Timber.d("Hiding connectivity message")
				}
			}
			.forUi(this)
			.subscribe {
				snackbar?.run { dismiss() }
				it.ifPresent {
					snackbar = indefiniteErrorSnackbar(coordinatorLayout, it).apply { show() }
				}
			}

		swipeRefreshLayout.refreshes()
			.doOnNext { Timber.i("Refreshing...") }
			.forUi(this)
			.subscribe(viewModel.refresh)
	}
}
