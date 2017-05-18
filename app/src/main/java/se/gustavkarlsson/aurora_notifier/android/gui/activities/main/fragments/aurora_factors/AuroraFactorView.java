package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.R;


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
				CharSequence name = auroraFactorViewAttributes.getText(R.styleable.AuroraFactorView_name);
				TextView nameView = (TextView) findViewById(R.id.aurora_factor_name);
				nameView.setText(name);
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

	public void setValue(String value) {
		factorValueView.setText(value);
	}
}
