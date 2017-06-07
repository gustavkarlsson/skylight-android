package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import se.gustavkarlsson.skylight.android.R


class AuroraFactorView : LinearLayout {
    private lateinit var factorValueView: TextView

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @SuppressLint("SetTextI18n")
	private fun init(context: Context, attrs: AttributeSet?) {
        View.inflate(getContext(), R.layout.view_aurora_factor, this)
        if (attrs != null) {
            val auroraFactorViewAttributes = context.obtainStyledAttributes(attrs, R.styleable.AuroraFactorView)
            try {
                setCompactTitle(auroraFactorViewAttributes.getText(R.styleable.AuroraFactorView_title_compact))
                setBadgeIcon(auroraFactorViewAttributes.getDrawable(R.styleable.AuroraFactorView_icon))
            } finally {
                auroraFactorViewAttributes.recycle()
            }
        }
        factorValueView = findViewById(R.id.aurora_factor_value) as TextView
        // Used by IDE to display something
        if (isInEditMode) {
            factorValueView.text = "value"
        }
    }

    private fun setCompactTitle(text: CharSequence) {
        val compactTitleView = findViewById(R.id.aurora_factor_title_compact) as TextView
        compactTitleView.text = text
    }

    private fun setBadgeIcon(icon: Drawable) {
        val badge = findViewById(R.id.badge) as ImageView
        badge.setImageDrawable(icon)
    }

    fun setValue(value: String) {
        factorValueView.text = value
    }
}
