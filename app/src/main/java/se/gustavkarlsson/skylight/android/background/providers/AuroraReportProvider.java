package se.gustavkarlsson.skylight.android.background.providers;

import org.threeten.bp.Duration;

import se.gustavkarlsson.skylight.android.models.AuroraReport;

public interface AuroraReportProvider {
	AuroraReport getReport(Duration timeout);
}
