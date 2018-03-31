package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import com.jakewharton.rxbinding2.widget.textRes
import kotlinx.android.synthetic.main.fragment_aurora_chance.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.Skylight
import se.gustavkarlsson.skylight.android.extensions.forUi
import timber.log.Timber


class AuroraChanceFragment : Fragment() {

	private val viewModel: AuroraChanceViewModel by lazy {
		val factory = Skylight.instance.component.getAuroraChanceViewModelFactory()
		ViewModelProviders.of(this, factory).get(AuroraChanceViewModel::class.java)
	}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_aurora_chance, container)
    }

	override fun onStart() {
		super.onStart()
		bindData()
	}

	private fun bindData() {
		viewModel.chanceLevel
			.doOnNext { Timber.d("Updating chanceLevel view") }
			.forUi(this)
			.subscribe(chance.textRes())

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.forUi(this)
			.subscribe(timeSinceUpdate.text())

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate visibility: %s", it) }
			.forUi(this)
			.subscribe(timeSinceUpdate.visibility())
	}
}
