package se.gustavkarlsson.skylight.android.gui.activities.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.startActivity
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.actions.PresentRecentAuroraReport
import se.gustavkarlsson.skylight.android.actions.PresentingErrors
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity
import javax.inject.Inject

class MainActivity : AuroraRequirementsCheckingActivity() {

    @Inject
	lateinit var swipeToRefreshController: SwipeToRefreshController

	@Inject
	lateinit var presentingErrors: PresentingErrors

	@Inject
	lateinit var presentRecentAuroraReport: PresentRecentAuroraReport

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
        return when (item.itemId) {
            R.id.action_settings -> run { startActivity<SettingsActivity>(); true }
			else                 -> super.onOptionsItemSelected(item)
        }
    }

    public override fun onStart() {
        super.onStart()
		presentingErrors.start()
        swipeToRefreshController.disable()
        ensureRequirementsMet()
    }

    override fun onRequirementsMet() {
        swipeToRefreshController.enable()
		bg { presentRecentAuroraReport() }
    }

    override fun onStop() {
        super.onStop()
		presentingErrors.stop()
    }
}
