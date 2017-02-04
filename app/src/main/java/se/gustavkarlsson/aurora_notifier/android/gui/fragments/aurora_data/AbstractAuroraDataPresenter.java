package se.gustavkarlsson.aurora_notifier.android.gui.fragments.aurora_data;

import se.gustavkarlsson.aurora_notifier.android.gui.views.AuroraDataView;

abstract class AbstractAuroraDataPresenter {
	private final AuroraDataView dataView;

	AbstractAuroraDataPresenter(AuroraDataView dataView) {
		this.dataView = dataView;
	}

	void setDataValue(String value) {
		dataView.setValue(value);
	}
}
