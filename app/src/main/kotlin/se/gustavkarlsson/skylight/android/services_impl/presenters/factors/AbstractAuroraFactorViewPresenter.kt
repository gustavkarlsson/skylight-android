package se.gustavkarlsson.skylight.android.services_impl.presenters.factors


import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.alert
import org.jetbrains.anko.okButton
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors.AuroraFactorView
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter

abstract class AbstractAuroraFactorViewPresenter<F>(
	private val factorView: AuroraFactorView,
	private val chanceEvaluator: ChanceEvaluator<F>,
	private val colorConverter: ChanceToColorConverter
) : Presenter<F> {

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

    override fun present(value: F) {
		async(UI) {
			setFactorValue(evaluateText(value))
			setFactorChance(chanceEvaluator.evaluate(value))
		}
    }

    private fun setFactorValue(value: String) {
		factorView.setValue(value)
    }

    private fun setFactorChance(chance: Chance) {
        val color = colorConverter.convert(chance)
        val badge = factorView.findViewById<View>(R.id.badge)
        val background = badge.background
        background.mutate()
		when (background) {
			is ShapeDrawable    -> background.paint.color = color
			is GradientDrawable -> background.setColor(color)
			is ColorDrawable    -> background.color = color
		}
    }

    abstract val fullTitleResourceId: Int

    abstract val descriptionResourceId: Int

    abstract fun evaluateText(factor: F): String
}
