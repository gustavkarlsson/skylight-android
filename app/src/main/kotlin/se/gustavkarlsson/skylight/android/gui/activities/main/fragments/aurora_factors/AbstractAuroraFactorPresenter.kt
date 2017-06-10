package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors


import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.v7.app.AlertDialog
import android.view.View
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.evaluation.Chance
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator

abstract class AbstractAuroraFactorPresenter<in T>(
		private val factorView: AuroraFactorView,
		private val chanceEvaluator: ChanceEvaluator<T>,
		private val colorConverter: ChanceToColorConverter
) {

	// TODO fix unsafe use of abstract methods
    init {
        val context = factorView.context
        val fullTitle = context.getString(fullTitleResourceId)
        val description = context.getString(descriptionResourceId)
        this.factorView.setOnClickListener(PopupDescriptionClickListener(context, fullTitle, description))
    }

    fun update(factor: T) {
        setFactorValue(evaluateText(factor))
        setFactorChance(chanceEvaluator.evaluate(factor))
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
			is ShapeDrawable -> background.paint.color = color
			is GradientDrawable -> background.setColor(color)
			is ColorDrawable -> background.color = color
		}
    }

    internal abstract val fullTitleResourceId: Int

    internal abstract val descriptionResourceId: Int

    internal abstract fun evaluateText(factor: T): String
}

private class PopupDescriptionClickListener(
		context: Context,
		title: String,
		description: String
) : View.OnClickListener {

	private val dialog: AlertDialog

	init {
		dialog = buildDialog(context, title, description)
	}

	private fun buildDialog(context: Context, title: String, description: String): AlertDialog {
		return AlertDialog.Builder(context)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(title)
				.setMessage(description)
				.setPositiveButton(android.R.string.yes) { dialog, _ -> dialog.dismiss() }
				.setCancelable(true)
				.create()
	}

	override fun onClick(v: View) = dialog.show()
}
