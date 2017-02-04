package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;

abstract class AbstractAuroraDataPresenter {
	private final AuroraDataView dataView;

	AbstractAuroraDataPresenter(AuroraDataView dataView) {
		this.dataView = dataView;
		Context context = dataView.getContext();
		String title = context.getString(getTitleResourceId());
		String description = context.getString(getDescriptionResourceId());
		this.dataView.setOnClickListener(new PopupDescriptionClickListener(context, title, description));
	}

	void setDataValue(String value) {
		dataView.setValue(value);
	}

	abstract int getTitleResourceId();

	abstract int getDescriptionResourceId();

	private static class PopupDescriptionClickListener implements View.OnClickListener {
		private final Context context;
		private final String title;
		private final String description;

		PopupDescriptionClickListener(Context context, String title, String description) {
			this.context = context;
			this.title = title;
			this.description = description;
		}

		@Override
		public void onClick(View v) {
			new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(title)
					.setMessage(description)
					.setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
					.setCancelable(true)
					.show();
		}
	}
}
