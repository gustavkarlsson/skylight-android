package se.gustavkarlsson.skylight.android.gui.screens.main

import android.animation.ObjectAnimator
import android.content.res.ColorStateList
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.text
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import timber.log.Timber
import kotlin.math.roundToInt


class FactorPresenter(
	private val values: Flowable<CharSequence>,
	private val chances: Flowable<Chance>,
	private val valueView: TextView,
	private val progressBar: ProgressBar,
	private val cardView: CardView,
	private val showDialog: () -> Unit,
	private val scope: LifecycleScopeProvider<*>,
	private val factorDebugName: String
) {
	fun present() {
		presentValues()
		presentChances()
		presentCardClicks()
	}

	private fun presentValues() {
		values
			.doOnNext { Timber.d("Updating %s value view: %s", factorDebugName, it) }
			.autoDisposable(scope)
			.subscribe(valueView.text())
	}

	private fun presentChances() {
		val chanceToColorConverter = ChanceToColorConverter(cardView.context)
		val maxProgress = progressBar.max
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
	}

	private fun ProgressBar.animateProgress(progress: Int) {
		val animation = ObjectAnimator.ofInt(this, "progress", progress)
		animation.duration = 1000
		animation.interpolator = AccelerateDecelerateInterpolator()
		animation.setAutoCancel(true)
		animation.start()
	}

	private fun presentCardClicks() {
		cardView.clicks()
			.autoDisposable(scope)
			.subscribe { showDialog() }
	}
}
