package se.gustavkarlsson.aurora_notifier.android.cache;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public interface ReportNotificationCache {

	AuroraReport getLastNotified();

	void setLastNotified(AuroraReport report);
}
