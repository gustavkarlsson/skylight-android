package se.gustavkarlsson.skylight.android.gui.activities.main

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.background.Updater
import se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.FOREGROUND_REPORT_LIFETIME_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_ERROR_NAME
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
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
        Log.v(TAG, "onCreate")
        super.onCreate(savedInstanceState)
        component = Skylight.applicationComponent.getMainActivityComponent(ActivityModule(this))
        setContentView(R.layout.activity_main)
        component.inject(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        Log.v(TAG, "onCreateOptionsMenu")
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.v(TAG, "onOptionsItemSelected")
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    public override fun onStart() {
        Log.v(TAG, "onStart")
        super.onStart()
        broadcastManager.registerReceiver(broadcastReceiver, IntentFilter(Updater.RESPONSE_UPDATE_ERROR))
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

	// TODO use operator extension function arithmetic
    private fun needsUpdate(report: AuroraReport): Boolean {
        val hasExpired = clock.millis() - report.timestampMillis > foregroundReportLifetime.toMillis()
        val isUnknown = !auroraChanceEvaluator.evaluate(report).isKnown
        return hasExpired || isUnknown
    }

    private fun updateInBackground() {
        cachedTreadPool.execute { updater.update(backgoundUpdateTimeout) }
    }

    override fun onStop() {
        Log.v(TAG, "onStop")
        super.onStop()
        broadcastManager.unregisterReceiver(broadcastReceiver)
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}
