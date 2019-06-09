package se.gustavkarlsson.skylight.android

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.lib.ui.Navigator


internal class MainActivity : AppCompatActivity() {

	private val navigator by inject<Navigator>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		addToKoin<Activity>(this)
		addToKoin(supportFragmentManager)
		setContentView(R.layout.activity_main)
		navigator.navigate("main", false)
	}

	override fun onBackPressed() {
		if (navigator.onBackPressed()) return
		super.onBackPressed()
	}
}
