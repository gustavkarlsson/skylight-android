package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.fragmentContainer
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.lib.navigation.NavItem
import se.gustavkarlsson.skylight.android.lib.navigation.Navigator


internal class MainActivity : AppCompatActivity() {

	private val navigator by inject<Navigator>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		addToKoin<Activity>(this)
		addToKoin(supportFragmentManager)
		setContentView(R.layout.activity_main)
		addToKoin<ViewGroup>(fragmentContainer)
		navigator.push(NavItem("main"))
	}

	override fun onBackPressed() = navigator.onBackPressed()
}
