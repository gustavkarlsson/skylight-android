package se.gustavkarlsson.aurora_notifier.android.gui.activities.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.R;

public class SettingsActivity extends AppCompatActivity {
	private static final String TAG = SettingsActivity.class.getSimpleName();

	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		realm = Realm.getDefaultInstance();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		realm.close();
		super.onDestroy();
	}
}
