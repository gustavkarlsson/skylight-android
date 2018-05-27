package se.gustavkarlsson.skylight.android.gui.screens.main

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.support.v7.widget.CardView
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import timber.log.Timber
import kotlin.math.roundToInt


class FactorPresenter(
	private val values: Observable<CharSequence>,
	private val chances: Observable<Chance>,
	private val valueView: TextView,
	private val progressBar: ProgressBar,
	private val cardView: CardView,
	private val clickConsumer: Consumer<Unit>,
	private val scope: LifecycleScopeProvider<*>,
	private val factorDebugName: String
) {
	fun present() {
		val chanceToColorConverter = ChanceToColorConverter(cardView.context)
		val maxProgress = progressBar.max

		values
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.autoDisposable(scope)
			.subscribe(valueView.text())

		chances
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.autoDisposable(scope)
			.subscribe {
				val color = chanceToColorConverter.convert(it)
				progressBar.progressTintList = ColorStateList.valueOf(color)

				val value = it.value
				if (value == null) {
					progressBar.progress = 0
				} else {
					val progress = (value * maxProgress).roundToInt()
					progressBar.animateProgress(progress)
				}
			}

		cardView.clicks()
			.autoDisposable(scope)
			.subscribe(clickConsumer)
	}

	private fun ProgressBar.animateProgress(progress: Int) {
		val animation = ObjectAnimator.ofInt(this, "progress", progress)
		animation.duration = 1000
		animation.interpolator = AccelerateDecelerateInterpolator()
		animation.setAutoCancel(true)
		animation.start()
	}
}
