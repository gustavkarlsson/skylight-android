package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_chance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.visibility
import com.jakewharton.rxbinding2.widget.text
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_aurora_chance.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.appComponent
import se.gustavkarlsson.skylight.android.gui.AutoDisposingFragment
import timber.log.Timber


class AuroraChanceFragment : AutoDisposingFragment() {

	private val viewModel: AuroraChanceViewModel by lazy {
		appComponent.auroraChanceViewModel(this)
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
			.doOnNext { Timber.d("Updating chanceLevel view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(chance.text())
			.autoDisposeOnStop()

		viewModel.timeSinceUpdate
			.doOnNext { Timber.d("Updating timeSinceUpdate view: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.text())
			.autoDisposeOnStop()

		viewModel.timeSinceUpdateVisibility
			.doOnNext { Timber.d("Updating timeSinceUpdate visibility: %s", it) }
			.observeOn(AndroidSchedulers.mainThread())
			.subscribe(timeSinceUpdate.visibility())
			.autoDisposeOnStop()
	}
}
