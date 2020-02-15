package se.gustavkarlsson.skylight.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator

// TODO Not necessary
private const val HAS_RUN_KEY = "has_run"

internal class MainActivity : AppCompatActivity() {

	private val navigator by inject<Navigator>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		addToKoin<FragmentActivity>(this)
		setContentView(R.layout.activity_main)
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
