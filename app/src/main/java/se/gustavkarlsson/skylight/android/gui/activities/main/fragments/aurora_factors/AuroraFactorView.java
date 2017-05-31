package se.gustavkarlsson.skylight.android.gui.activities.main.fragments.aurora_factors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.gustavkarlsson.skylight.android.R;


public class AuroraFactorView extends LinearLayout {
	private TextView factorValueView;

	public AuroraFactorView(Context context) {
		super(context);
		init(context, null);
	}

	public AuroraFactorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AuroraFactorView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@SuppressLint("SetTextI18n")
	private void init(Context context, AttributeSet attrs) {
		inflate(getContext(), R.layout.view_aurora_factor, this);
		if (attrs != null) {
			TypedArray auroraFactorViewAttributes = context.obtainStyledAttributes(attrs, R.styleable.AuroraFactorView);
			try {
				setCompactTitle(auroraFactorViewAttributes.getText(R.styleable.AuroraFactorView_title_compact));
				setBadgeIcon(auroraFactorViewAttributes.getDrawable(R.styleable.AuroraFactorView_icon));
			} finally {
				auroraFactorViewAttributes.recycle();
			}
		}
		factorValueView = (TextView) findViewById(R.id.aurora_factor_value);
		// Used by IDE to display something
		if (isInEditMode()) {
			factorValueView.setText("value");
		}
	}

	private void setCompactTitle(CharSequence text) {
		TextView compactTitleView = (TextView) findViewById(R.id.aurora_factor_title_compact);
		compactTitleView.setText(text);
	}

	private void setBadgeIcon(Drawable icon) {
		ImageView badge = (ImageView) findViewById(R.id.badge);
		badge.setImageDrawable(icon);
	}

	public void setValue(String value) {
		factorValueView.setText(value);
	}
}
