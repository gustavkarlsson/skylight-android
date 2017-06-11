package se.gustavkarlsson.skylight.android.gui.activities.settings

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.dagger.components.SettingsActivityComponent

class SettingsActivity : AppCompatActivity() {

	lateinit var component: SettingsActivityComponent
		private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		component = Skylight.applicationComponent.getSettingsActivityComponent()
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}
