package se.gustavkarlsson.aurora_notifier.android.cache;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public interface AuroraReportCache {

	AuroraReport get();

	void set(AuroraReport report);
}
