package se.gustavkarlsson.skylight.android.cache;

import se.gustavkarlsson.skylight.android.models.AuroraReport;

public interface ReportNotificationCache {

	AuroraReport getLastNotified();

	void setLastNotified(AuroraReport report);
}
