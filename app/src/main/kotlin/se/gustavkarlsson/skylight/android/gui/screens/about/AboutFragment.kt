package se.gustavkarlsson.skylight.android.gui.screens.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity

class AboutFragment : Fragment(), LifecycleObserver {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		lifecycle.addObserver(this)
	}

	override fun onStart() {
		super.onStart()
		appCompatActivity!!.supportActionBar!!.hide()
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_about, container, false)

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {

	}

	override fun onDestroy() {
		lifecycle.removeObserver(this)
		super.onDestroy()
	}
}
