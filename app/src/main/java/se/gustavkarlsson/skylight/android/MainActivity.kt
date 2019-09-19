package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.fragmentContainer
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

const val HAS_RUN_KEY = "has_run"

internal class MainActivity : AppCompatActivity() {

	private val navigator by inject<Navigator>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		addToKoin<Activity>(this)
		addToKoin(supportFragmentManager)
		setContentView(R.layout.activity_main)
		addToKoin<ViewGroup>(fragmentContainer)
		if (savedInstanceState?.containsKey(HAS_RUN_KEY) != true) {
			navigator.push(NavItem("main"))
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		outState.putBoolean(HAS_RUN_KEY, true)
		super.onSaveInstanceState(outState)
	}

	override fun onBackPressed() = navigator.onBackPressed()
}
