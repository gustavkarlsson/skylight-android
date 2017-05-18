package se.gustavkarlsson.aurora_notifier.android.gui.activities.main.fragments.aurora_factors;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;

import se.gustavkarlsson.aurora_notifier.android.evaluation.AuroraChance;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.AuroraChanceToColorConverter;

import static com.google.common.base.Preconditions.checkNotNull;

abstract class AbstractAuroraFactorPresenter {
	private final AuroraFactorView factorView;
	private final AuroraChanceToColorConverter colorConverter;

	AbstractAuroraFactorPresenter(AuroraFactorView factorView) {
		this.factorView = checkNotNull(factorView, "factorView may not be null");
		this.colorConverter = new AuroraChanceToColorConverter(factorView.getContext());
		Context context = factorView.getContext();
		String title = context.getString(getTitleResourceId());
		String description = context.getString(getDescriptionResourceId());
		this.factorView.setOnClickListener(new PopupDescriptionClickListener(context, title, description));
	}

	void setFactorValue(String value) {
		factorView.setValue(value);
	}

	void setBackgroundColor(AuroraChance auroraChance) {
		int color = colorConverter.convert(auroraChance);
		factorView.setBackgroundColor(color);
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
