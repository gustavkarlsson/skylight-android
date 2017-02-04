package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_complications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraComplicationsFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraComplicationsFragment.class.getSimpleName();

	@BindView(R.id.fragment_aurora_complications_root_view)
	RelativeLayout rootView;

	@BindView(R.id.complications_list_view)
	ListView complicationsListView;

	private ComplicationsPresenter complicationsPresenter;
	private Unbinder unbinder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		View rootView = inflater.inflate(R.layout.fragment_aurora_complications, container, false);
		unbinder = ButterKnife.bind(this, rootView);
		complicationsPresenter = new ComplicationsPresenter(complicationsListView, getContext());
		return rootView;
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		complicationsPresenter.update(evaluation.getComplications());
		rootView.invalidate();
	}

	@Override
	public void onDestroyView() {
		Log.v(TAG, "onDestroyView");
		unbinder.unbind();
		super.onDestroyView();
	}
}
