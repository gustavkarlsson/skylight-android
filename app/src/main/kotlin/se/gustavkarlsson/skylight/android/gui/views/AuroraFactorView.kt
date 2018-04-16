package se.gustavkarlsson.skylight.android.gui.views

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_aurora_factor.view.*
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.util.ChanceToColorConverter


class AuroraFactorView : LinearLayout {

	private val chanceToColorConverter: ChanceToColorConverter

    constructor(context: Context) : super(context) {
		chanceToColorConverter = ChanceToColorConverter(context)
		init(context, null)
	}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		chanceToColorConverter = ChanceToColorConverter(context)
		init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
		chanceToColorConverter = ChanceToColorConverter(context)
		init(context, attrs)
    }

	private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(getContext(), R.layout.view_aurora_factor, this)
        if (attrs != null) {
            val auroraFactorViewAttributes = context.obtainStyledAttributes(attrs, R.styleable.AuroraFactorView)
			auroraFactorBadge.background.mutate()
            try {
                name = auroraFactorViewAttributes.getText(R.styleable.AuroraFactorView_name)
                badgeIcon = auroraFactorViewAttributes.getDrawable(R.styleable.AuroraFactorView_badgeIcon)
				val chanceFloat = auroraFactorViewAttributes.getFloat(R.styleable.AuroraFactorView_chance, Float.NaN)
				chance = if (chanceFloat.isNaN()) Chance.UNKNOWN else Chance(chanceFloat.toDouble())
				value = auroraFactorViewAttributes.getText(R.styleable.AuroraFactorView_value)
            } finally {
                auroraFactorViewAttributes.recycle()
            }
        }
        // Used by IDE to display something
        if (isInEditMode) {
			value = "value"
        }
    }

	var name: CharSequence?
		get() = auroraFactorName.text
		set(value) {
			auroraFactorName.text = value
		}

	var badgeIcon: Drawable?
		get() = auroraFactorBadge.drawable
		set(value) {
			auroraFactorBadge.setImageDrawable(value)
		}

	private var _chance = Chance.UNKNOWN
	var chance: Chance
		get() = _chance
		set(value) {
			val background = auroraFactorBadge.background
			val color = chanceToColorConverter.convert(value)
			when (background) {
				is ShapeDrawable -> background.paint.color = color
				is GradientDrawable -> background.setColor(color)
				is ColorDrawable -> background.color = color
			}
			_chance = value
		}

	var value: CharSequence?
		get() = auroraFactorValue.text
		set(value) {
			auroraFactorValue.text = value
		}
}
