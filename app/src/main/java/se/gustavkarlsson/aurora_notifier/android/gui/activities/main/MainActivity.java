package se.gustavkarlsson.aurora_notifier.android.gui.activities.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.cache.AuroraReportCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerMainActivityComponent;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraReportUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.AuroraRequirementsCheckingActivity;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.settings.SettingsActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

import static se.gustavkarlsson.aurora_notifier.android.AuroraNotifier.getApplicationComponent;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_ERROR;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_FINISHED;
import static se.gustavkarlsson.aurora_notifier.android.background.Updater.RESPONSE_UPDATE_FINISHED_EXTRA_REPORT;

public class MainActivity extends AuroraRequirementsCheckingActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	Updater updater;

	@Inject
	AuroraReportCache AuroraReportCache;

	@Inject
	LocalBroadcastManager broadcastManager;

	private int reportLifetimeMillis;
	private int backgroundUpdateTimeoutMillis;
	private SwipeToRefreshPresenter swipeToRefreshPresenter;
	private List<AuroraReportUpdateListener> updateReceivers;
	private BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		DaggerMainActivityComponent.builder()
				.applicationComponent(getApplicationComponent(this))
				.build()
				.inject(this);

		reportLifetimeMillis = getResources().getInteger(R.integer.setting_foreground_report_lifetime_millis);
		backgroundUpdateTimeoutMillis = getResources().getInteger(R.integer.setting_background_update_timeout_millis);
		swipeToRefreshPresenter = new SwipeToRefreshPresenter((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout), this, updater);
		updateReceivers = getUpdateReceivers();
		broadcastReceiver = createBroadcastReceiver();
	}

	private List<AuroraReportUpdateListener> getUpdateReceivers() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<AuroraReportUpdateListener> updateReceivers = new LinkedList<>();
		updateReceivers.add((AuroraReportUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_chance));
		updateReceivers.add((AuroraReportUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_factors));
		return updateReceivers;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (RESPONSE_UPDATE_FINISHED.equals(action)) {
					Parcelable reportParcel = intent.getParcelableExtra(RESPONSE_UPDATE_FINISHED_EXTRA_REPORT);
					AuroraReport report = Parcels.unwrap(reportParcel);
					updateGui(report);
				} else if (RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				}
			}
		};
	}

	private void updateGui(AuroraReport report) {
		for (AuroraReportUpdateListener receiver : updateReceivers) {
			receiver.onUpdate(report);
		}
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
		broadcastManager.registerReceiver((broadcastReceiver), new IntentFilter(RESPONSE_UPDATE_ERROR));
		broadcastManager.registerReceiver((broadcastReceiver), new IntentFilter(RESPONSE_UPDATE_FINISHED));
		AuroraReport report = getBestReport();
		swipeToRefreshPresenter.disable();
		ensureRequirementsMet();
		updateGui(report);
	}

	@Override
	protected void onRequirementsMet() {
		swipeToRefreshPresenter.enable();
		AuroraReport report = getBestReport();
		long ageMillis = System.currentTimeMillis() - report.getTimestampMillis();
		if (ageMillis > reportLifetimeMillis) {
			updateInBackground();
		}
	}

	private AuroraReport getBestReport() {
		AuroraReport report = AuroraReportCache.get();
		if (report != null) {
			return report;
		}
		return AuroraReport.createFallback();
	}

	private void updateInBackground() {
		AsyncTask.execute(() -> updater.update(backgroundUpdateTimeoutMillis));
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
		super.onDestroy();
	}
}
