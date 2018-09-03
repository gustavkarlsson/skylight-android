package se.gustavkarlsson.skylight.android.gui.screens.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.kotlin.autoDisposable
import kotlinx.android.synthetic.main.fragment_intro.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.extensions.appCompatActivity
import se.gustavkarlsson.skylight.android.krate.SignalFirstRunCompleted

class IntroFragment : Fragment(), LifecycleObserver {

	// TODO Move to ViewModel
	private val store by lazy {
		appComponent.store
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		appCompatActivity!!.supportActionBar!!.hide()
		lifecycle.addObserver(this)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? = inflater.inflate(R.layout.fragment_intro, container, false)

	@OnLifecycleEvent(Lifecycle.Event.ON_START)
	private fun bindData() {
		nextButton.clicks()
			.autoDisposable(scope(Lifecycle.Event.ON_STOP))
			.subscribe {
				store.issue(SignalFirstRunCompleted)
			}
	}

	override fun onDestroy() {
		super.onDestroy()
		lifecycle.removeObserver(this)
	}
}
