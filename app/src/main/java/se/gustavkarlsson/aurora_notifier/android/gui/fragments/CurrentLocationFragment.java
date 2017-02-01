package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.background.Updater;
import se.gustavkarlsson.aurora_notifier.android.caching.PersistentCache;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.CurrentLocationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerCurrentLocationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.PersistentCacheModule;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

import static java.util.Collections.singletonList;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private static final String STATE_AURORA_EVALUATION = "STATE_AURORA_EVALUATION";
	private static final String CACHE_KEY_EVALUATION = "CACHE_KEY_EVALUATION";
	private static final long TIME_RESOLUTION_MILLIS = DateUtils.MINUTE_IN_MILLIS;

	private final ServiceConnection updaterConnection = new UpdaterConnection();

	@Inject
	PersistentCache<Parcelable> evaluationPersistentCache;

	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver;
	private ComplicationsListAdapter complicationsAdapter;
	private int evaluationLifeMillis;
	private AuroraEvaluation evaluation;

	@BindView(R.id.root_layout)
	View rootView;

	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R.id.bottom_sheet_layout)
	RelativeLayout bottomSheetLayout;

	@BindView(R.id.complications_list_view)
	ListView complicationsListView;

	@BindView(R.id.chance)
	TextView chanceTextView;

	@BindView(R.id.time_since_update)
	TextView timeSinceUpdateTextView;

	@BindView(R.id.location)
	TextView locationTextView;

	private Unbinder unbinder;
	private BottomSheetBehavior bottomSheetBehavior;
	private Timer timeUpdateTimer;

	private Updater updater;
	private boolean updaterBound;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		CurrentLocationComponent component = DaggerCurrentLocationComponent.builder()
				.persistentCacheModule(new PersistentCacheModule(getContext(), getResources().getInteger(R.integer.cache_size_bytes)))
				.build();
		component.inject(this);
		broadcastManager = LocalBroadcastManager.getInstance(getContext());
		broadcastReceiver = createBroadcastReceiver();
		complicationsAdapter = new ComplicationsListAdapter(getContext());
		evaluationLifeMillis = getResources().getInteger(R.integer.foreground_evaluation_life_millis);
		evaluation = getSavedEvaluation(savedInstanceState);
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (swipeRefreshLayout != null) {
					swipeRefreshLayout.setRefreshing(false);
				}
				String action = intent.getAction();
				if (UpdateService.RESPONSE_UPDATE_FINISHED.equals(action)) {
					Parcelable evaluationParcel = intent.getParcelableExtra(UpdateService.RESPONSE_UPDATE_FINISHED_EXTRA_EVALUATION);
					evaluation = Parcels.unwrap(evaluationParcel);
					updateViews();
				} else if (UpdateService.RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(UpdateService.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private AuroraEvaluation getSavedEvaluation(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			Parcelable parcelable = savedInstanceState.getParcelable(STATE_AURORA_EVALUATION);
			return Parcels.unwrap(parcelable);
		}
		if (evaluationPersistentCache.exists(CACHE_KEY_EVALUATION)) {
			Parcelable parcelable = evaluationPersistentCache.get(CACHE_KEY_EVALUATION);
			AuroraEvaluation evaluation = Parcels.unwrap(parcelable);
			if (evaluation != null) {
				return evaluation;
			}
		}
		return createUnknownEvaluation();
	}

	private static AuroraEvaluation createUnknownEvaluation() {
		AuroraData data = new AuroraData(
				new SolarActivity(0),
				new GeomagneticLocation(0),
				new SunPosition(0),
				new Weather(0)
		);
		AuroraComplication unknownComplication = new AuroraComplication(
				AuroraChance.UNKNOWN,
				R.string.complication_no_data_title,
				R.string.complication_no_data_desc);
		return new AuroraEvaluation(0, null, data, singletonList(unknownComplication));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_current_location, container, false);
		unbinder = ButterKnife.bind(this, rootView);

		setupSwipeToRefresh();
		bottomSheetBehavior = createBottomSheetBehavior();
		complicationsListView.setAdapter(complicationsAdapter);
		updateViews();
		return rootView;
	}

	private void updateViews() {
		updateLocationView();
		updateTimeSinceUpdate();
		scheduleTimeSinceUpdateRefresh();
		chanceTextView.setText(evaluation.getChance().getResourceId());
		complicationsAdapter.setItems(evaluation.getComplications());
		complicationsAdapter.notifyDataSetChanged();
		updateBottomSheetState();
		rootView.invalidate();
	}

	private void scheduleTimeSinceUpdateRefresh() {
		if (timeUpdateTimer != null) {
			timeUpdateTimer.cancel();
		}
		timeUpdateTimer = new Timer();
		timeUpdateTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSinceUpdateTextView.post(() -> {
					updateTimeSinceUpdate();
				});
			}
		}, 1000L, TIME_RESOLUTION_MILLIS);
	}

	private void updateTimeSinceUpdate() {
		if (isJustNow(evaluation.getTimestampMillis())) {
			timeSinceUpdateTextView.setText(R.string.just_now);
			return;
		}
		CharSequence text = formatRelativeTime(evaluation.getTimestampMillis());
		timeSinceUpdateTextView.setText(text);
		timeSinceUpdateTextView.invalidate();
	}

	private static boolean isJustNow(long timestampMillis) {
		long ageMillis = System.currentTimeMillis() - timestampMillis;
		return ageMillis <= TIME_RESOLUTION_MILLIS;
	}

	private static CharSequence formatRelativeTime(long startTimeMillis) {
		return DateUtils.getRelativeTimeSpanString(startTimeMillis, System.currentTimeMillis(), TIME_RESOLUTION_MILLIS);
	}

	private void updateLocationView() {
		Address address = evaluation.getAddress();
		if (address == null) {
			locationTextView.setVisibility(View.INVISIBLE);
		} else {
			locationTextView.setVisibility(View.VISIBLE);
			String locationString = address.getLocality();
			locationTextView.setText(locationString);
		}
	}

	private void updateBottomSheetState() {
		if (evaluation.getComplications().isEmpty()) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		} else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}
	}

	private void setupSwipeToRefresh() {
		swipeRefreshLayout.setOnRefreshListener(() -> {
			if (updaterBound) {
				swipeRefreshLayout.setRefreshing(true);
				AsyncTask.execute(updater::update);
			}
		});
	}

	private BottomSheetBehavior createBottomSheetBehavior() {
		final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
		ensureSizeIsRecalculatedOnInteraction(bottomSheetBehavior);
		return bottomSheetBehavior;
	}

	@OnClick(R.id.bottom_sheet_layout)
	void onClick() {
		if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
		}
	}

	//Workaround for bug described in http://stackoverflow.com/a/40267305/940731
	private static void ensureSizeIsRecalculatedOnInteraction(BottomSheetBehavior bottomSheetBehavior) {
		bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull final View bottomSheet, int newState) {
				bottomSheet.post(() -> {
					bottomSheet.requestLayout();
					bottomSheet.invalidate();
				});
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {
			}
		});
	}

	@OnItemClick(R.id.complications_list_view)
	public void onItemClick(int position) {
		AuroraComplication complication = evaluation.getComplications().get(position);
		new AlertDialog.Builder(getContext())
				.setTitle(complication.getTitleStringResource())
				.setMessage(complication.getDescriptionStringResource())
				.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
				.show();
	}

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.RESPONSE_UPDATE_FINISHED));
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.RESPONSE_UPDATE_ERROR));
		bindUpdater();
	}

	private void bindUpdater() {
		Intent intent = new Intent(getContext(), UpdateService.class);
		getActivity().bindService(intent, updaterConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		swipeRefreshLayout.setRefreshing(false);
		broadcastManager.unregisterReceiver(broadcastReceiver);
		unbindUpdater();
		super.onStop();
	}

	private void unbindUpdater() {
		if (updaterBound) {
			getActivity().unbindService(updaterConnection);
			updaterBound = false;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState");
		Parcelable parcel = Parcels.wrap(evaluation);
		outState.putParcelable(STATE_AURORA_EVALUATION, parcel);
		Parcelable parcelable = Parcels.wrap(evaluation);
		evaluationPersistentCache.set(CACHE_KEY_EVALUATION, parcelable);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		timeUpdateTimer.cancel();
		swipeRefreshLayout.setOnRefreshListener(null);
		unbinder.unbind();
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.v(TAG, "onDestroy");
		try {
			evaluationPersistentCache.close();
		} catch (IOException e) {
			Log.e(TAG, "Failed to close cache", e);
		}
		super.onDestroy();
	}

	private boolean evaluationExpired() {
		long expiryTime = evaluation.getTimestampMillis() + evaluationLifeMillis;
		return System.currentTimeMillis() > expiryTime;
	}

	private class UpdaterConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.v(TAG, "onServiceConnected");
			UpdateService.UpdaterBinder updaterBinder = (UpdateService.UpdaterBinder) service;
			updater = updaterBinder.getUpdater();
			updaterBound = true;
			if (evaluationExpired()) {
				AsyncTask.execute(updater::update);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.v(TAG, "onServiceDisconnected");
			updaterBound = false;
		}

	}
}
