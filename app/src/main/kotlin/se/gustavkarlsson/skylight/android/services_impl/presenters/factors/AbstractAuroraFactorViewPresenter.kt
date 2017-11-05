package se.gustavkarlsson.skylight.android.services_impl.presenters.factors


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
				iconResource = R.drawable.info_white_24dp
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
		factorView.value = value
    }

    private fun setFactorChance(chance: Chance) {
        val color = colorConverter.convert(chance)
		factorView.badgeColor = color
    }

    abstract val fullTitleResourceId: Int

    abstract val descriptionResourceId: Int

    abstract fun evaluateText(factor: F): String
}
