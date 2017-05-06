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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.BuildConfig;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.DebugActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.realm.EvaluationCache;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = MainActivity.class.getSimpleName();

	private int evaluationLifetimeMillis;
	private int backgroundUpdateTimeoutMillis;
	private SwipeToRefreshPresenter swipeToRefreshPresenter;
	private List<AuroraEvaluationUpdateListener> updateReceivers;
	private BroadcastReceiver broadcastReceiver;
	private BottomSheetPresenter bottomSheetPresenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		evaluationLifetimeMillis = getResources().getInteger(R.integer.foreground_evaluation_lifetime_millis);
		backgroundUpdateTimeoutMillis = getResources().getInteger(R.integer.background_update_timeout_millis);
		swipeToRefreshPresenter = new SwipeToRefreshPresenter((SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout), this);
		updateReceivers = getUpdateReceivers();
		bottomSheetPresenter = new BottomSheetPresenter(findViewById(R.id.bottom_sheet));
		broadcastReceiver = createBroadcastReceiver();
	}

	private List<AuroraEvaluationUpdateListener> getUpdateReceivers() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		List<AuroraEvaluationUpdateListener> updateReceivers = new LinkedList<>();
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_chance));
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_data));
		updateReceivers.add((AuroraEvaluationUpdateListener) fragmentManager.findFragmentById(R.id.fragment_aurora_complications));
		return updateReceivers;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (Updater.RESPONSE_UPDATE_FINISHED.equals(action)) {
					Parcelable evaluationParcel = intent.getParcelableExtra(Updater.RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION);
					AuroraEvaluation evaluation = Parcels.unwrap(evaluationParcel);
					updateGui(evaluation);
				} else if (Updater.RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(Updater.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
				}
			}
		};
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(Updater.RESPONSE_UPDATE_FINISHED));
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(Updater.RESPONSE_UPDATE_ERROR));
		return broadcastReceiver;
	}

	private void updateGui(AuroraEvaluation evaluation) {
		bottomSheetPresenter.onUpdate(evaluation.getComplications());
		for (AuroraEvaluationUpdateListener receiver : updateReceivers) {
			receiver.onUpdate(evaluation);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.v(TAG, "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu");
		if (!BuildConfig.DEBUG) {
			menu.removeItem(R.id.action_debug);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG, "onOptionsItemSelected");
		int id = item.getItemId();

		if (id == R.id.action_debug) {
			Intent intent = new Intent(this, DebugActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		AuroraEvaluation evaluation = getBestEvaluation();
		updateGui(evaluation);
		swipeToRefreshPresenter.onStart();
	}

	private AuroraEvaluation getBestEvaluation() {
		AuroraEvaluation evaluation = EvaluationCache.get();
		if (evaluation != null) {
			return evaluation;
		}
		return AuroraEvaluation.createFallback();
	}

	@Override
	protected void onResume() {
		Log.v(TAG, "onResume");
		super.onResume();
		AuroraEvaluation evaluation = EvaluationCache.get();
		long evaluationTimestampMillis = evaluation == null ? 0 : evaluation.getTimestampMillis();
		long ageMillis = System.currentTimeMillis() - evaluationTimestampMillis;
		if (ageMillis > evaluationLifetimeMillis) {
			updateInBackground();
		}
	}

	private void updateInBackground() {
		AsyncTask.execute(() -> {
			Updater updater = new Updater(this, backgroundUpdateTimeoutMillis);
			updater.update();
		});
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		swipeToRefreshPresenter.onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.v(TAG, "onDestroy");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onDestroy();
	}
}
