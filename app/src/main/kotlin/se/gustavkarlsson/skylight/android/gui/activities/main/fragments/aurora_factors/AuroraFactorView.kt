package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.support.v4.app.ActivityCompat
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_aurora_factor.view.*
import se.gustavkarlsson.skylight.android.R


class AuroraFactorView : LinearLayout {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
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
				val defaultBadgeColor = ActivityCompat.getColor(context, R.color.chance_unknown)
				badgeColor = auroraFactorViewAttributes.getColor(R.styleable.AuroraFactorView_badgeColor, defaultBadgeColor)
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

	private var _badgeColor: Int = 0
	var badgeColor: Int
		get() = _badgeColor
		set(value) {
			_badgeColor = value
			val background = auroraFactorBadge.background
			when (background) {
				is ShapeDrawable -> background.paint.color = _badgeColor
				is GradientDrawable -> background.setColor(_badgeColor)
				is ColorDrawable -> background.color = _badgeColor
			}
		}

	var value: CharSequence?
		get() = auroraFactorValue.text
		set(value) {
			auroraFactorValue.text = value
		}
}
