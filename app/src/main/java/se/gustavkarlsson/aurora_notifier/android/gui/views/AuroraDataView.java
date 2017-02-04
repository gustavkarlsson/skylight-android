package se.gustavkarlsson.aurora_notifier.android.gui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import se.gustavkarlsson.aurora_notifier.android.R;


public class AuroraDataView extends LinearLayout {
	private ImageView dataImageView;
	private TextView dataValueView;

	public AuroraDataView(Context context) {
		super(context);
		setViews();
	}

	public AuroraDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setViews();
	}

	public AuroraDataView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setViews();
	}

	private void setViews() {
		inflate(getContext(), R.layout.view_aurora_data, this);
		dataImageView = (ImageView) findViewById(R.id.aurora_data_image);
		dataValueView = (TextView) findViewById(R.id.aurora_data_value);
	}

	public void setImage(int resourceId) {
		dataImageView.setImageResource(resourceId);
	}

	public void setValue(String value) {
		dataValueView.setText(value);
	}
}
