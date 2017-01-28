package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
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

	private static final String STATE_AURORA_EVALUATION = TAG + ".STATE_AURORA_EVALUATION";

	private LocalBroadcastManager broadcastManager;
	private BroadcastReceiver broadcastReceiver;
	private AuroraEvaluation auroraEvaluation;
	private ComplicationsListAdapter complicationsAdapter;

	@BindView(R.id.swipe_refresh_layout)
	SwipeRefreshLayout swipeRefreshLayout;

	@BindView(R.id.bottom_sheet_layout)
	RelativeLayout bottomSheetLayout;

	@BindView(R.id.complications_list_view)
	ListView complicationsListView;

	@BindView(R.id.chance)
	TextView chanceTextView;

	private Unbinder unbinder;
	private BottomSheetBehavior bottomSheetBehavior;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		broadcastManager = LocalBroadcastManager.getInstance(getContext());
		broadcastReceiver = createBroadcastReceiver();
		if (savedInstanceState == null) {
			auroraEvaluation = createUpdatingEvaluation();
		} else {
			Parcelable parcel = savedInstanceState.getParcelable(STATE_AURORA_EVALUATION);
			auroraEvaluation = Parcels.unwrap(parcel);
		}
		complicationsAdapter = new ComplicationsListAdapter(getContext());
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
					auroraEvaluation = Parcels.unwrap(evaluationParcel);
					updateViews();
				} else if (UpdateService.RESPONSE_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(UpdateService.RESPONSE_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private static AuroraEvaluation createUpdatingEvaluation() {
		AuroraData data = new AuroraData(
				new SolarActivity(0),
				new GeomagneticLocation(0),
				new SunPosition(0),
				new Weather(0)
		);
		AuroraComplication updatingComplication = new AuroraComplication(
				AuroraChance.UNKNOWN,
				R.string.complication_updating_title,
				R.string.complication_updating_desc);
		return new AuroraEvaluation(System.currentTimeMillis(), data, singletonList(updatingComplication));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_current_location, container, false);
		ButterKnife.setDebug(true);
		unbinder = ButterKnife.bind(this, rootView);

		setupSwipeToRefresh();
		bottomSheetBehavior = createBottomSheetBehavior();
		complicationsListView.setAdapter(complicationsAdapter);
		updateViews();
		return rootView;
	}

	private void updateViews() {
		chanceTextView.setText(auroraEvaluation.getChance().getResourceId());
		chanceTextView.invalidate();
		complicationsAdapter.setItems(auroraEvaluation.getComplications());
		complicationsAdapter.notifyDataSetChanged();
		if (auroraEvaluation.getComplications().isEmpty()) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
		} else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}
	}

	private void setupSwipeToRefresh() {
		swipeRefreshLayout.setOnRefreshListener(() -> {
			swipeRefreshLayout.setRefreshing(true);
			UpdateService.start(getContext());
		});
	}

	private BottomSheetBehavior createBottomSheetBehavior() {
		final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
		bottomSheetLayout.setOnClickListener(view -> {
			if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
		});
		ensureSizeIsRecalculatedOnInteraction(bottomSheetBehavior);
		return bottomSheetBehavior;
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
		AuroraComplication complication = auroraEvaluation.getComplications().get(position);
		new AlertDialog.Builder(getContext())
				.setTitle(complication.getTitleStringResource())
				.setMessage(complication.getDescriptionStringResource())
				.setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
				.setIcon(android.R.drawable.ic_dialog_info)
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
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		broadcastManager.unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.v(TAG, "onSaveInstanceState");
		Parcelable parcel = Parcels.wrap(auroraEvaluation);
		outState.putParcelable(STATE_AURORA_EVALUATION, parcel);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		swipeRefreshLayout.setOnRefreshListener(null);
		unbinder.unbind();
		super.onDestroyView();
	}

}
