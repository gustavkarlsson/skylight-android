package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_complications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class ComplicationsListAdapter extends BaseAdapter {
	private final LayoutInflater inflater;
	private List<AuroraComplication> complications = Collections.emptyList();

	ComplicationsListAdapter(Context context) {
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return complications.size();
	}

	@Override
	public AuroraComplication getItem(int position) {
		return complications.get(position);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = inflater.inflate(R.layout.view_complication, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder.titleTextView.setText(complications.get(position).getTitleStringResource());
		return convertView;
	}

	void setItems(List<AuroraComplication> complications) {
		this.complications = complications;
	}

	static class ViewHolder {
		@BindView(R.id.complication_title) TextView titleTextView;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}
}
