package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.longToast
import org.jetbrains.anko.startActivity
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.dagger.FOREGROUND_REPORT_LIFETIME_NAME
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import se.gustavkarlsson.skylight.android.services.Stream
import se.gustavkarlsson.skylight.android.services.StreamSubscription
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

class MainActivity : AuroraRequirementsCheckingActivity() {

    @Inject
	lateinit var swipeToRefreshPresenter: SwipeToRefreshPresenter

	@Inject
	lateinit var userFriendlyExceptions: Stream<UserFriendlyException>

	@Inject
	lateinit var showRecentAuroraReport: ShowRecentAuroraReport

    @Inject
    @field:Named(FOREGROUND_REPORT_LIFETIME_NAME)
	lateinit var foregroundReportLifetime: Duration

	lateinit var component: MainActivityComponent
        private set

	var userFriendlyExceptionsSubscription: StreamSubscription? = null

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
        return when (item.itemId) {
            R.id.action_settings -> run { startActivity<SettingsActivity>(); true }
			else                 -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onStart() {
        super.onStart()
		userFriendlyExceptionsSubscription = userFriendlyExceptions.subscribe {
			async(UI) {
				longToast(it.stringResourceId)
			}
		}
        swipeToRefreshPresenter.disable()
        ensureRequirementsMet()
    }

    override fun onRequirementsMet() {
        swipeToRefreshPresenter.enable()
		showRecentAuroraReport()
    }

    override fun onStop() {
        super.onStop()
		userFriendlyExceptionsSubscription?.cancel()
    }
}
