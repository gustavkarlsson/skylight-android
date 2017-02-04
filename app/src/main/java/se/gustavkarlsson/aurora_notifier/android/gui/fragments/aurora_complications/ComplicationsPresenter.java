package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_complications;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraComplication;

class ComplicationsPresenter {
	private final Context context;
	private final ComplicationsListAdapter complicationsListAdapter;

	ComplicationsPresenter(ListView complicationsListView) {
		context = complicationsListView.getContext();
		complicationsListAdapter = new ComplicationsListAdapter(context);
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
