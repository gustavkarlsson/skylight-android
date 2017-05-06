package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_complications;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.AuroraEvaluationUpdateListener;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraEvaluation;

public class AuroraComplicationsFragment extends Fragment implements AuroraEvaluationUpdateListener {
	private static final String TAG = AuroraComplicationsFragment.class.getSimpleName();

	private View rootView;
	private ComplicationsPresenter complicationsPresenter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView");
		rootView = inflater.inflate(R.layout.fragment_aurora_complications, container, false);
		complicationsPresenter = new ComplicationsPresenter((ListView) rootView.findViewById(R.id.complications_list_view));
		return rootView;
	}

	@Override
	public void onUpdate(AuroraEvaluation evaluation) {
		complicationsPresenter.onUpdate(evaluation.getComplications());
		rootView.invalidate();
	}
}
