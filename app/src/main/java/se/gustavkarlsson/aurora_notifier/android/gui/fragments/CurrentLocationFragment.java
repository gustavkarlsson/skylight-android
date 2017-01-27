package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.BindingAdapter;
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
import android.widget.Toast;

import org.parceler.Parcels;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.UpdateService;
import se.gustavkarlsson.aurora_notifier.android.databinding.FragmentCurrentLocationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.AuroraEvaluationViewModel;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.PermissionUtils;

import static java.util.Collections.singletonList;

public class CurrentLocationFragment extends Fragment {
	private static final String TAG = CurrentLocationFragment.class.getSimpleName();

	private static final String STATE_AURORA_EVALUATION = TAG + ".STATE_AURORA_EVALUATION";

	private AuroraEvaluation auroraEvaluation;
	private AuroraEvaluationViewModel auroraEvaluationViewModel;
	private BroadcastReceiver broadcastReceiver;
	private SwipeRefreshLayout swipeView;
	private BottomSheetBehavior bottomSheetBehavior;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			auroraEvaluation = createUpdatingEvaluation();
			if (PermissionUtils.hasLocationPermission(getActivity())) {
				UpdateService.requestUpdate(getContext());
			}
		} else {
			Parcelable parcel = savedInstanceState.getParcelable(STATE_AURORA_EVALUATION);
			auroraEvaluation = Parcels.unwrap(parcel);
			if (AuroraChance.UNKNOWN == auroraEvaluation.getChance()) {
				UpdateService.requestUpdate(getContext());
			}
		}
		auroraEvaluationViewModel = new AuroraEvaluationViewModel(auroraEvaluation);
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
		FragmentCurrentLocationBinding binding = FragmentCurrentLocationBinding.inflate(inflater, container, false);
		binding.setEvaluation(auroraEvaluationViewModel);
		final View rootView = binding.getRoot();
		broadcastReceiver = createBroadcastReceiver();
		swipeView = createSwipeRefresh(rootView);
		bottomSheetBehavior = setUpBottomSheetBehavior(rootView);
		setUpComplicationOnClickListener(rootView);
		return rootView;
	}

	private BroadcastReceiver createBroadcastReceiver() {
		return new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				swipeView.setRefreshing(false);
				String action = intent.getAction();
				if (UpdateService.ACTION_UPDATE_FINISHED.equals(action)) {
					Parcelable evaluationParcel = intent.getParcelableExtra(UpdateService.ACTION_UPDATE_FINISHED_EXTRA_EVALUATION);
					auroraEvaluation = Parcels.unwrap(evaluationParcel);
					auroraEvaluationViewModel.update(auroraEvaluation);
					if (auroraEvaluation.getComplications().isEmpty()) {
						bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
					} else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
						bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
					}
				} else if (UpdateService.ACTION_UPDATE_ERROR.equals(action)) {
					String message = intent.getStringExtra(UpdateService.ACTION_UPDATE_ERROR_EXTRA_MESSAGE);
					Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private SwipeRefreshLayout createSwipeRefresh(View rootView) {
		final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		swipeView.setOnRefreshListener(() -> {
			swipeView.setRefreshing(true);
			UpdateService.requestUpdate(getContext());
		});
		return swipeView;
	}

	private static BottomSheetBehavior setUpBottomSheetBehavior(View rootView) {
		RelativeLayout bottomSheetLayout = (RelativeLayout) rootView.findViewById(R.id.linear_layout_bottom_sheet);
		final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
		bottomSheetLayout.setOnClickListener(view -> {
			if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
				bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
			}
		});
		ensureSizeIsRecalculatedOnInteraction(bottomSheetBehavior);
		return bottomSheetBehavior;
	}

	private void setUpComplicationOnClickListener(View rootView) {
		ListView listView = (ListView) rootView.findViewById(R.id.aurora_complications);
		listView.setOnItemClickListener((parent, view, position, id) -> {
            AuroraComplication complication = (AuroraComplication) parent.getItemAtPosition(position);
            new AlertDialog.Builder(getContext())
                    .setTitle(complication.getTitleStringResource())
                    .setMessage(complication.getDescriptionStringResource())
                    .setPositiveButton(R.string.ok, (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        });
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

	@Override
	public void onStart() {
		Log.v(TAG, "onStart");
		super.onStart();
		LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.ACTION_UPDATE_FINISHED));
		broadcastManager.registerReceiver((broadcastReceiver),
				new IntentFilter(UpdateService.ACTION_UPDATE_ERROR));
	}

	@Override
	public void onStop() {
		Log.v(TAG, "onStop");
		LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
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
		swipeView.setOnRefreshListener(null);
		super.onDestroyView();
	}

	@BindingAdapter("bind:items")
	public static void bindList(ListView listView, List<AuroraComplication> complications) {
		ComplicationsListAdapter adapter = new ComplicationsListAdapter(complications);
		listView.setAdapter(adapter);
	}

}
