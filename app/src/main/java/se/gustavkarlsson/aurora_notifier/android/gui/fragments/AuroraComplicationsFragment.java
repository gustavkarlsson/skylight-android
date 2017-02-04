package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationProvider;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraComplicationsFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraComplicationsFragment.class.getSimpleName();

	@BindView(R.id.fragment_aurora_complications_root_view)
	RelativeLayout rootView;

	@BindView(R.id.complications_list_view)
	ListView complicationsListView;

	private ComplicationsListAdapter complicationsAdapter;

	private AuroraEvaluationProvider evaluationProvider;
	private Unbinder unbinder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		complicationsAdapter = new ComplicationsListAdapter(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_complications, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		complicationsListView.setAdapter(complicationsAdapter);
		return rootView;
	}

	@Override
	public void onAttach(Context context) {
		Log.v(TAG, "onAttach");
		super.onAttach(context);
		evaluationProvider = (MainActivity) context;
	}

	@OnItemClick(R.id.complications_list_view)
	public void onItemClick(int position) {
		AuroraEvaluation evaluation = getEvaluation();
		if (evaluation == null) {
			return;
		}
		AuroraComplication complication = evaluation.getComplications().get(position);
		new AlertDialog.Builder(getContext())
				.setTitle(complication.getTitleStringResource())
				.setMessage(complication.getDescriptionStringResource())
				.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
				.show();
	}

	private AuroraEvaluation getEvaluation() {
		return evaluationProvider == null ? null : evaluationProvider.getEvaluation();
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		complicationsAdapter.setItems(evaluation.getComplications());
		complicationsAdapter.notifyDataSetChanged();
		rootView.invalidate();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		unbinder.unbind();
		super.onDestroyView();
	}
}
