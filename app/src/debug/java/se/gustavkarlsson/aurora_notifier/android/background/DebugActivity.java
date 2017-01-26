package se.gustavkarlsson.aurora_notifier.android.background;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class DebugActivity extends AppCompatActivity {
	private static final String TAG = DebugActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		//ActivityDebugBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_debug);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		super.onDestroy();
	}
}
