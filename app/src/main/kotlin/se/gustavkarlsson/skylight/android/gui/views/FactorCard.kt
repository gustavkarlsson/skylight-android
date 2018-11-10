package se.gustavkarlsson.skylight.android.gui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.layout_factor_card.view.progressBar
import kotlinx.android.synthetic.main.layout_factor_card.view.title
import kotlinx.android.synthetic.main.layout_factor_card.view.value
import se.gustavkarlsson.skylight.android.R

class FactorCard : FrameLayout {

	constructor(context: Context)
		: super(context, null) {
		init(context, null)
	}

	constructor(context: Context, attrs: AttributeSet?)
		: super(context, attrs) {
		init(context, attrs)
	}

	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
		: super(context, attrs, defStyleAttr) {
		init(context, attrs)
	}

	private fun init(context: Context, attrs: AttributeSet?) {
		val view = inflate(getContext(), R.layout.layout_factor_card, null) as CardView
		addView(view)
		context.theme.obtainStyledAttributes(attrs, R.styleable.FactorCard, 0, 0).apply {
			titleView.text = getString(R.styleable.FactorCard_title)
			recycle()
		}
	}

	private val titleView: TextView
		get() = title

	val valueView: TextView
		get() = value

	val progressView: ProgressBar
		get() = progressBar
}
