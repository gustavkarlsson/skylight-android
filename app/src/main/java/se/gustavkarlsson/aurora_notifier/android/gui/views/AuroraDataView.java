package se.gustavkarlsson.aurora_notifier.android.gui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.R;


public class AuroraDataView extends LinearLayout {
	private TextView dataValueView;

	public AuroraDataView(Context context) {
		super(context);
		init(context, null);
	}

	public AuroraDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AuroraDataView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@SuppressLint("SetTextI18n")
	private void init(Context context, AttributeSet attrs) {
		inflate(getContext(), R.layout.view_aurora_data, this);
		if (attrs != null) {
			TypedArray auroraDataViewAttributes = context.obtainStyledAttributes(attrs, R.styleable.AuroraDataView);
			try {
				Drawable drawable = auroraDataViewAttributes.getDrawable(R.styleable.AuroraDataView_drawable);
				ImageView imageView = (ImageView) findViewById(R.id.aurora_data_image);
				imageView.setImageDrawable(drawable);
			} finally {
				auroraDataViewAttributes.recycle();
			}
		}
		dataValueView = (TextView) findViewById(R.id.aurora_data_value);
		if (isInEditMode()) {
			dataValueView.setText("value");
		}
	}

	public void setValue(String value) {
		dataValueView.setText(value);
	}
}
