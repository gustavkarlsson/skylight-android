package se.gustavkarlsson.aurora_notifier.android.gui.fragments;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.databinding.ViewComplicationBinding;
import se.gustavkarlsson.aurora_notifier.android.gui.viewmodels.AuroraComplicationViewModel;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class ComplicationsListAdapter extends BaseAdapter {
	private final List<AuroraComplication> complications;

	ComplicationsListAdapter(List<AuroraComplication> complications) {
		this.complications = complications;
	}

	@Override
	public int getCount() {
		return complications.size();
	}

	@Override
	public Object getItem(int position) {
		return complications.get(position);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			return convertView;
		} else {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			ViewComplicationBinding binding = DataBindingUtil.inflate(inflater, R.layout.view_complication, parent, false);
			AuroraComplicationViewModel viewModel = new AuroraComplicationViewModel(complications.get(position));
			binding.setComplication(viewModel);
			return binding.getRoot();
		}
	}
}
