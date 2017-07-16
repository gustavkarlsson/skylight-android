package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors


import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator

abstract class AbstractAuroraFactorPresenter<in F>(
	private val factorView: AuroraFactorView,
	private val chanceEvaluator: ChanceEvaluator<F>,
	private val colorConverter: ChanceToColorConverter
) {

    init {
        this.factorView.setOnClickListener {
			factorView.context.alert {
				iconResource = android.R.drawable.ic_dialog_info
				title = ctx.getString(fullTitleResourceId)
				message = ctx.getString(descriptionResourceId)
				okButton { it.dismiss() }
			}.show()
		}
    }

    fun present(factor: F) {
		async(UI) {
			setFactorValue(evaluateText(factor))
			setFactorChance(chanceEvaluator.evaluate(factor))
		}
    }

    private fun setFactorValue(value: String) {
		factorView.setValue(value)
    }

    private fun setFactorChance(chance: Chance) {
        val color = colorConverter.convert(chance)
        val badge = factorView.findViewById(R.id.badge)
        val background = badge.background
        background.mutate()
		when (background) {
			is ShapeDrawable    -> background.paint.color = color
			is GradientDrawable -> background.setColor(color)
			is ColorDrawable    -> background.color = color
		}
    }

    internal abstract val fullTitleResourceId: Int

    internal abstract val descriptionResourceId: Int

    internal abstract fun evaluateText(factor: F): String
}
