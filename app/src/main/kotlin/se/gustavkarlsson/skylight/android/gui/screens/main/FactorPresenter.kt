package se.gustavkarlsson.skylight.android.gui.screens.main

import android.content.res.ColorStateList
import com.jakewharton.rxbinding2.view.clicks
import com.uber.autodispose.LifecycleScopeProvider
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.gui.views.FactorCard
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter
import timber.log.Timber
import kotlin.math.roundToInt


class FactorPresenter(
	private val values: Flowable<CharSequence>,
	private val chances: Flowable<Chance>,
	private val cardView: FactorCard,
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
			.subscribe {
				cardView.valueView.text = it
			}
	}

	private fun presentChances() {
		val chanceToColorConverter = ChanceToColorConverter(cardView.context)
		val maxProgress = (cardView.progressView.max * 0.95).roundToInt()
		val minProgress = (cardView.progressView.max - maxProgress)
		chances
			.doOnNext { Timber.d("Updating %s chance view: %s", factorDebugName, it) }
			.autoDisposable(scope)
			.subscribe { chance ->
				val color = chanceToColorConverter.convert(chance)
				cardView.progressView.progressTintList = ColorStateList.valueOf(color)

				val progress = getProgress(chance, minProgress, maxProgress)
				cardView.progressView.progress = progress
			}
	}

	private fun getProgress(chance: Chance, minProgress: Int, maxProgress: Int) =
		chance.value?.let { chanceValue ->
			(chanceValue * maxProgress).roundToInt() + minProgress
		} ?: 0

	private fun presentCardClicks() {
		cardView.clicks()
			.autoDisposable(scope)
			.subscribe { showDialog() }
	}
}
