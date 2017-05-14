package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public interface AuroraReportProvider {
	AuroraReport getReport(long timeoutMillis);
}
