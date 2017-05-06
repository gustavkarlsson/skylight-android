package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class AbstractAuroraDataPresenter {
	private final AuroraDataView dataView;

	AbstractAuroraDataPresenter(AuroraDataView dataView) {
		this.dataView = checkNotNull(dataView, "dataView may not be null");
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
		private AlertDialog dialog;

		PopupDescriptionClickListener(Context context, String title, String description) {
			dialog = buildDialog(context, title, description);
		}

		private static AlertDialog buildDialog(Context context, String title, String description) {
			return new AlertDialog.Builder(context)
					.setIcon(android.R.drawable.ic_dialog_info)
					.setTitle(title)
					.setMessage(description)
					.setPositiveButton(android.R.string.yes, (dialog, which) -> dialog.dismiss())
					.setCancelable(true)
					.create();
		}

		@Override
		public void onClick(View v) {
			dialog.show();
		}
	}
}
