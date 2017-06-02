package se.gustavkarlsson.skylight.android.cache;

import se.gustavkarlsson.skylight.android.models.AuroraReport;

public interface LastReportCache {

	AuroraReport get();

	void set(AuroraReport report);
}
