package se.gustavkarlsson.skylight.android.gui.activities.main

import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.startActivity
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.background.RESPONSE_UPDATE_ERROR
import se.gustavkarlsson.skylight.android.background.Updater
import se.gustavkarlsson.skylight.android.dagger.*
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.observers.ObservableValue
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named

class MainActivity : AuroraRequirementsCheckingActivity() {

    @Inject
    lateinit var updater: Updater

    @Inject
	lateinit var broadcastManager: LocalBroadcastManager

    @Inject
	lateinit var clock: Clock

    @Inject
	lateinit var swipeToRefreshPresenter: SwipeToRefreshPresenter

    @Inject
    @field:Named(LATEST_NAME)
	lateinit var latestAuroraReport: ObservableValue<AuroraReport>

    @Inject
	lateinit var auroraChanceEvaluator: ChanceEvaluator<AuroraReport>

    @Inject
    @field:Named(UPDATE_ERROR_NAME)
	lateinit var broadcastReceiver: BroadcastReceiver

    @Inject
    @field:Named(CACHED_THREAD_POOL_NAME)
	lateinit var cachedTreadPool: ExecutorService

    @Inject
    @field:Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
	lateinit var backgoundUpdateTimeout: Duration

    @Inject
    @field:Named(FOREGROUND_REPORT_LIFETIME_NAME)
	lateinit var foregroundReportLifetime: Duration

	lateinit var component: MainActivityComponent
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component = Skylight.applicationComponent.getMainActivityComponent(ActivityModule(this))
        setContentView(R.layout.activity_main)
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
				startActivity<SettingsActivity>()
                return true
            }
			else -> return super.onOptionsItemSelected(item)
        }
    }

    public override fun onStart() {
        super.onStart()
        broadcastManager.registerReceiver(broadcastReceiver, IntentFilter(RESPONSE_UPDATE_ERROR))
        swipeToRefreshPresenter.disable()
        ensureRequirementsMet()
    }

    override fun onRequirementsMet() {
        swipeToRefreshPresenter.enable()
        val latestReport = latestAuroraReport.value
        if (needsUpdate(latestReport)) {
            updateInBackground()
        }
    }

    private fun needsUpdate(report: AuroraReport): Boolean {
        val hasExpired = report.timestamp until clock.now > foregroundReportLifetime
		val chance = auroraChanceEvaluator.evaluate(report)
        return hasExpired || !chance.isKnown
    }

    private fun updateInBackground() {
        cachedTreadPool.execute { updater.update(backgoundUpdateTimeout) }
    }

    override fun onStop() {
        super.onStop()
        broadcastManager.unregisterReceiver(broadcastReceiver)
    }
}
