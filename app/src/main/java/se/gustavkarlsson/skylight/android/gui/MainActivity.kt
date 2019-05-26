package se.gustavkarlsson.skylight.android.gui

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.ext.android.bindScope
import org.koin.androidx.scope.ext.android.createScope
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.addToKoin
import se.gustavkarlsson.skylight.android.feature.base.BackButtonHandler
import se.gustavkarlsson.skylight.android.feature.base.Navigator


class MainActivity : AppCompatActivity() {

	private val navigator by inject<Navigator>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		bindScope(createScope("activity"))
		addToKoin<Activity>(this)
		addToKoin(supportFragmentManager)
		setContentView(R.layout.activity_main)
		navigator.navigate("main")
	}

	override fun onBackPressed() {
		// FIXME Move to navigator?
		val topFragment = supportFragmentManager.fragments.lastOrNull()
		if (topFragment is BackButtonHandler && topFragment.onBackPressed()) return
		super.onBackPressed()
	}
}
