package se.gustavkarlsson.skylight.android.gui.activities.main;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;

import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.Updater;
import se.gustavkarlsson.skylight.android.dagger.components.MainActivityComponent;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ActivityModule;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity;
import se.gustavkarlsson.skylight.android.gui.activities.settings.SettingsActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.observers.ObservableData;

import static se.gustavkarlsson.skylight.android.Skylight.getApplicationComponent;
import static se.gustavkarlsson.skylight.android.background.Updater.RESPONSE_UPDATE_ERROR;
import static se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.CACHED_THREAD_POOL_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.LATEST_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_ERROR_NAME;

public class MainActivity extends AuroraRequirementsCheckingActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private static final Duration REPORT_LIFETIME = Duration.ofMinutes(15);

	@Inject
	Updater updater;

	@Inject
	LocalBroadcastManager broadcastManager;

	@Inject
	Clock clock;

	@Inject
	SwipeToRefreshPresenter swipeToRefreshPresenter;

	@Inject
	@Named(LATEST_NAME)
	ObservableData<AuroraReport> latestAuroraReport;

	@Inject
	ChanceEvaluator<AuroraReport> auroraChanceEvaluator;

	@Inject
	@Named(UPDATE_ERROR_NAME)
	BroadcastReceiver broadcastReceiver;

	@Inject
	@Named(CACHED_THREAD_POOL_NAME)
	ExecutorService cachedTreadPool;

	@Inject
	@Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
	Duration backgoundUpdateTimeout;

	private MainActivityComponent component;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		component = getApplicationComponent().getMainActivityComponent(new ActivityModule(this));
		setContentView(R.layout.activity_main);
		component.inject(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		switch (item.getItemId()) {
			case R.id.action_settings: {
				Intent intent = new Intent(this, SettingsActivity.class);
				startActivity(intent);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		broadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(RESPONSE_UPDATE_ERROR));
		swipeToRefreshPresenter.disable();
		ensureRequirementsMet();
	}

	@Override
	protected void onRequirementsMet() {
		swipeToRefreshPresenter.enable();
		AuroraReport latestReport = latestAuroraReport.getData();
		if (needsUpdate(latestReport)) {
			updateInBackground();
		}
	}

	private boolean needsUpdate(AuroraReport report) {
		boolean hasExpired = clock.millis() - report.getTimestampMillis() > REPORT_LIFETIME.toMillis();
		boolean isUnknown = !auroraChanceEvaluator.evaluate(report).isKnown();
		return hasExpired || isUnknown;
	}

	private void updateInBackground() {
		cachedTreadPool.execute(() -> updater.update(backgoundUpdateTimeout.toMillis()));
	}

	@Override
	protected void onStop() {
		Log.v(TAG, "onStop");
		super.onStop();
		broadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		updater = null;
		broadcastManager = null;
		swipeToRefreshPresenter = null;
		broadcastReceiver = null;
		component = null;
		latestAuroraReport = null;
		clock = null;
		super.onDestroy();
	}

	public MainActivityComponent getComponent() {
		return component;
	}
}
