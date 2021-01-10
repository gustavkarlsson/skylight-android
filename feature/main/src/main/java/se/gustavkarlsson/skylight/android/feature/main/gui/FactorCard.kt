package se.gustavkarlsson.skylight.android.feature.main.gui

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.layout_factor_card.view.*
import se.gustavkarlsson.skylight.android.feature.main.R
import se.gustavkarlsson.skylight.android.lib.ui.extensions.toArgb
import kotlin.math.roundToInt

internal class FactorCard : FrameLayout {

    constructor(context: Context) : super(context, null) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val view = inflate(
            getContext(),
            R.layout.layout_factor_card,
            null,
        ) as MaterialCardView
        addView(view)
        context.theme.obtainStyledAttributes(attrs, R.styleable.FactorCard, 0, 0)
            .apply {
                title.text = getString(R.styleable.FactorCard_title)
                recycle()
            }
    }

    override fun hasOnClickListeners() = card.hasOnClickListeners()

    override fun setOnClickListener(listener: OnClickListener?) = card.setOnClickListener(listener)

    override fun setOnLongClickListener(listener: OnLongClickListener?) =
        card.setOnLongClickListener(listener)

    override fun setOnContextClickListener(listener: OnContextClickListener?) =
        card.setOnContextClickListener(listener)

    fun setItem(item: FactorItem) {
        val totalProgress = (progressBar.max * 0.97).roundToInt()
        val minProgress = (progressBar.max - totalProgress)

        value.text = item.valueText.resolve(context)
        value.setTextColor(item.valueTextColor.toArgb(context))
        progressBar.progressTintList = ColorStateList.valueOf(item.progressColor)
        progressBar.progress = item.progress?.let { progress ->
            (progress * totalProgress).roundToInt() + minProgress
        } ?: 0
    }
}
