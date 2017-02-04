package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_complications;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class ComplicationsPresenter {
	private final ComplicationsListAdapter complicationsListAdapter;
	private final Context context;

	ComplicationsPresenter(ListView complicationsListView, Context context) {
		this.complicationsListAdapter = new ComplicationsListAdapter(context);
		this.context = context;
		complicationsListView.setAdapter(complicationsListAdapter);
		complicationsListView.setOnItemClickListener(new PopupDescriptionClickListener());
	}

	void update(List<AuroraComplication> complications) {
		complicationsListAdapter.setItems(complications);
		complicationsListAdapter.notifyDataSetChanged();
	}

	private class PopupDescriptionClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AuroraComplication complication = complicationsListAdapter.getItem(position);
			new AlertDialog.Builder(context)
					.setTitle(complication.getTitleStringResource())
					.setMessage(complication.getDescriptionStringResource())
					.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
					.show();
		}
	}
}
